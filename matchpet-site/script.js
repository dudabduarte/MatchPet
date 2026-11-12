
(() => {
  'use strict';

  const STORAGE = {
    favorites: 'matchpet.favorites.v1',
    requests: 'matchpet.requests.v1'
  };

  const API_URL = 'http://localhost:8080/api/pets';

  const photos = {
    tobias: 'imagem/cachorro-inteligente.jpeg',
    luna: 'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=900&q=82',
    kiko: 'https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=900&q=82',
    mel: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=900&q=82',
    nina: 'https://images.unsplash.com/photo-1533738363-b7f9aef128ce?auto=format&fit=crop&w=900&q=82',
    fred: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=900&q=82',
    bela: 'https://images.unsplash.com/photo-1495360010541-f48722b34f7d?auto=format&fit=crop&w=900&q=82',
    theo: 'https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?auto=format&fit=crop&w=900&q=82',
    pipoca: 'https://images.unsplash.com/photo-1585110396000-c9ffd4e4b308?auto=format&fit=crop&w=900&q=82',
    kiwi: 'https://images.unsplash.com/photo-1552728089-57bdde30beb3?auto=format&fit=crop&w=900&q=82'
  };

  const read = (key, fallback) => {
    try {
      const value = localStorage.getItem(key);
      return value ? JSON.parse(value) : fallback;
    } catch {
      return fallback;
    }
  };

  // Os pets agora vêm da API Java.
  let pets = [];

  let favorites = read(STORAGE.favorites, []);
  if (!Array.isArray(favorites)) favorites = [];

  let requests = read(STORAGE.requests, []);
  if (!Array.isArray(requests)) requests = [];

  const state = {
    species: 'Todos',
    onlyFavorites: false,
    location: '',
    visibleCount: 8,
    toastTimer: null
  };

  const $ = (selector, root = document) => root.querySelector(selector);
  const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];

  const petGrid = $('#petGrid');
  const petModal = $('#petModal');
  const formModal = $('#formModal');

  const escapeHtml = value =>
      String(value ?? '').replace(/[&<>"']/g, char => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
      })[char]);

  const photoStyle = image =>
      `background-image:url("${escapeHtml(image || photos.tobias)}")`;

  // Comunicação com a API Java.
  async function apiRequest(url, options = {}) {
    const response = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      }
    });

    if (!response.ok) {
      const error = await response.text();
      throw new Error(error || `Erro HTTP: ${response.status}`);
    }

    if (response.status === 204) {
      return null;
    }

    return response.json();
  }

  // Converte o Pet.java para o formato usado pelo frontend.
  function converterPet(pet) {
    const caracteristicas = pet.caracteristicas || [];
    const idade = pet.idade ?? 0;

    return {
      id: pet.id,
      name: pet.nome,
      species: pet.especie,
      age: idade,

      city: pet.localizacao?.cidade || '',
      state: pet.localizacao?.estado || '',

      traits: caracteristicas,

      breed: pet.especie || 'Não informada',
      size: 'Não informado',
      sex: '',

      image: photos.tobias,
      description: caracteristicas.join(', '),
      shelter: 'Comunidade MatchPet',
      status: 'Pronto para adoção',

      ageGroup: idade === 0
          ? 'filhote'
          : idade < 7
              ? 'adulto'
              : 'idoso',

      isMine: true
    };
  }

  // Busca os animais cadastrados no banco.
  async function carregarPets() {
    try {
      const dados = await apiRequest(API_URL);

      pets = Array.isArray(dados)
          ? dados.map(converterPet)
          : [];

      populateCities();
      renderPets();

    } catch (error) {
      console.error('Erro ao carregar pets:', error);

      pets = [];
      populateCities();
      renderPets();

      showToast('Não foi possível carregar os pets. Verifique o servidor e o banco de dados.');
    }
  }

  function showToast(message) {
    const toast = $('#toast');

    toast.textContent = message;
    toast.classList.add('show');

    clearTimeout(state.toastTimer);

    state.toastTimer = setTimeout(
        () => toast.classList.remove('show'),
        2600
    );
  }

  function updateFavoriteCount() {
    $('#favoriteCount').textContent = favorites.length;
  }

  function populateCities() {
    const select = $('#cityFilter');
    const current = select.value;

    const cities = [
      ...new Set(pets.map(pet => pet.city).filter(Boolean))
    ].sort((a, b) => a.localeCompare(b, 'pt-BR'));

    select.innerHTML =
        '<option value="">Qualquer cidade</option>' +
        cities.map(city =>
            `<option value="${escapeHtml(city)}">${escapeHtml(city)}</option>`
        ).join('');

    if (cities.includes(current)) {
      select.value = current;
    }
  }

  function matchingPets() {
    const query = $('#nameSearch').value.trim().toLocaleLowerCase('pt-BR');
    const size = $('#sizeFilter').value;
    const age = $('#ageFilter').value;
    const city = $('#cityFilter').value;

    const location = state.location.trim().toLocaleLowerCase('pt-BR');

    return pets.filter(pet => {
      const searchable =
          `${pet.name} ${pet.breed} ${pet.city} ${pet.state}`
              .toLocaleLowerCase('pt-BR');

      return (
          (state.species === 'Todos' || pet.species === state.species) &&
          (!query || searchable.includes(query)) &&
          (!location ||
              `${pet.city} ${pet.state}`.toLocaleLowerCase('pt-BR').includes(location)) &&
          (!size || pet.size === size) &&
          (!age || pet.ageGroup === age) &&
          (!city || pet.city === city) &&
          (!state.onlyFavorites || favorites.includes(pet.id))
      );
    });
  }

  function renderPets() {
    const results = matchingPets();
    const visible = results.slice(0, state.visibleCount);

    petGrid.innerHTML = visible.map(pet => `
      <article class="pet-card">
        <div class="pet-photo" style="${photoStyle(pet.image)}">
          <span class="pet-status">
            ${escapeHtml(pet.status || 'Pronto para adoção')}
          </span>

          <button
            class="favorite-button ${favorites.includes(pet.id) ? 'active' : ''}"
            data-favorite="${escapeHtml(pet.id)}"
            aria-label="${favorites.includes(pet.id) ? 'Remover dos' : 'Adicionar aos'} favoritos"
            aria-pressed="${favorites.includes(pet.id)}"
          >
            ${favorites.includes(pet.id) ? '♥' : '♡'}
          </button>
        </div>

        <div class="pet-card-body">
          <div class="pet-title-row">
            <h3>${escapeHtml(pet.name)}</h3>
            <span class="pet-gender">
              ${pet.sex === 'Macho' ? '♂' : pet.sex === 'Fêmea' ? '♀' : ''}
            </span>
          </div>

          <div class="pet-meta">
            ${escapeHtml(pet.breed || pet.species)} ·
            ${pet.age ? `${pet.age} ${pet.age === 1 ? 'ano' : 'anos'}` : 'menos de 1 ano'}
            · Porte ${escapeHtml(pet.size || 'Não informado')}
          </div>

          <p class="pet-description">
            ${escapeHtml(pet.description)}
          </p>

          <div class="pet-card-footer">
            <span class="pet-location">
              <span>⌖</span>
              ${escapeHtml(pet.city)}${pet.state ? `, ${escapeHtml(pet.state)}` : ''}
            </span>

            <button class="pet-details" data-details="${escapeHtml(pet.id)}">
              Conhecer <span aria-hidden="true">→</span>
            </button>
          </div>
        </div>
      </article>
    `).join('');

    $('#emptyState').classList.toggle('hidden', results.length > 0);
    petGrid.classList.toggle('hidden', results.length === 0);

    $('#resultsCount').textContent = state.onlyFavorites
        ? `${results.length} ${results.length === 1 ? 'favorito' : 'favoritos'}`
        : `${results.length} ${results.length === 1 ? 'pet esperando' : 'pets esperando'} por você`;

    $('#loadMore').classList.toggle(
        'hidden',
        results.length <= state.visibleCount
    );

    $('#favoritesOnly').classList.toggle('active', state.onlyFavorites);

    updateFavoriteCount();
  }

  function closeDialogs() {
    petModal.close();
    formModal.close();
  }

  function openPet(petId) {
    const pet = pets.find(item => item.id === petId);

    if (!pet) return;

    $('#petModalContent').innerHTML = `
      <div class="pet-modal-photo" style="${photoStyle(pet.image)}"></div>

      <div class="pet-modal-body">
        <p class="eyebrow">
          ${escapeHtml(pet.status || 'Pronto para adoção')}
        </p>

        <h2 id="petModalTitle">
          Oi, eu sou ${escapeHtml(pet.name)}
          <span aria-hidden="true">
            ${pet.sex === 'Macho' ? '♂' : pet.sex === 'Fêmea' ? '♀' : ''}
          </span>
        </h2>

        <div class="modal-meta">
          ${escapeHtml(pet.breed || pet.species)} ·
          ${pet.age ? `${pet.age} ${pet.age === 1 ? 'ano' : 'anos'}` : 'menos de 1 ano'}
          · Porte ${escapeHtml(pet.size || 'Não informado')}
          · ${escapeHtml(pet.city)}${pet.state ? `, ${escapeHtml(pet.state)}` : ''}
        </div>

        <p class="modal-description">
          ${escapeHtml(pet.description)}
        </p>

        <div class="modal-tags">
          ${(pet.traits || []).map(trait =>
        `<span>${escapeHtml(trait)}</span>`
    ).join('')}
        </div>

        <div class="shelter-box">
          <span>
            <strong>${escapeHtml(pet.shelter || 'ONG parceira')}</strong>
            Responsável pelo acolhimento
          </span>
          <span aria-hidden="true">♡</span>
        </div>

        <button class="button button-orange modal-cta" data-adopt="${escapeHtml(pet.id)}">
          Tenho interesse em conhecer
          <span aria-hidden="true">→</span>
        </button>

        ${pet.isMine ? `
          <div class="owner-actions">
            <button class="text-link" data-edit="${escapeHtml(pet.id)}">
              Editar cadastro
            </button>

            <button class="text-link" data-delete="${escapeHtml(pet.id)}">
              Remover pet
            </button>
          </div>
        ` : ''}

        <p class="form-hint">
          Protótipo demonstrativo: seu interesse fica salvo apenas neste navegador.
        </p>
      </div>
    `;

    petModal.showModal();
  }

  function petFormMarkup(pet = {}) {
    const editing = Boolean(pet.id);

    return `
      <div class="form-content">
        <p class="eyebrow">
          ${editing ? 'Atualizar perfil' : 'Uma nova história começa aqui'}
        </p>

        <h2 id="formModalTitle">
          ${editing ? `Editar ${escapeHtml(pet.name)}` : 'Cadastre um pet para adoção'}
        </h2>

        <p>
          Preencha os dados do animal para cadastrá-lo no MatchPet.
        </p>

        <form id="petForm" data-pet-id="${escapeHtml(pet.id || '')}">
          <div class="form-grid">

            <label class="form-field full">
              <span>Nome do pet *</span>
              <input
                name="nome"
                required
                maxlength="40"
                value="${escapeHtml(pet.name || '')}"
                placeholder="Ex.: Amora"
              >
            </label>

            <label class="form-field">
              <span>Espécie *</span>
              <select name="especie" required>
                <option value="">Selecione</option>
                <option value="Cachorro" ${pet.species === 'Cachorro' ? 'selected' : ''}>Cachorro</option>
                <option value="Gato" ${pet.species === 'Gato' ? 'selected' : ''}>Gato</option>
                <option value="Outros" ${pet.species === 'Outros' ? 'selected' : ''}>Outros animais</option>
              </select>
            </label>

            <label class="form-field">
              <span>Idade em anos *</span>
              <input
                name="idade"
                type="number"
                min="0"
                max="30"
                required
                value="${escapeHtml(pet.age ?? 0)}"
              >
            </label>

            <label class="form-field">
              <span>Cidade *</span>
              <input
                name="cidade"
                required
                maxlength="50"
                value="${escapeHtml(pet.city || '')}"
                placeholder="Ex.: Maringá"
              >
            </label>

            <label class="form-field">
              <span>Estado *</span>
              <input
                name="estado"
                required
                maxlength="2"
                minlength="2"
                value="${escapeHtml(pet.state || '')}"
                placeholder="Ex.: PR"
              >
            </label>

            <label class="form-field full">
              <span>Características do pet *</span>
              <textarea
                name="caracteristicas"
                required
                maxlength="300"
                placeholder="Separe as características por vírgula. Ex.: Dócil, brincalhão, vacinado"
              >${escapeHtml((pet.traits || []).join(', '))}</textarea>

              <small class="field-help">
                Separe cada característica com uma vírgula.
              </small>
            </label>

          </div>

          <button class="button button-orange" type="submit">
            ${editing ? 'Salvar alterações' : 'Cadastrar pet'}
            <span aria-hidden="true">→</span>
          </button>

          <p class="form-hint">
            Os campos correspondem à estrutura de cadastro do Java.
          </p>
        </form>
      </div>
    `;
  }

  function openPetForm(pet = null) {
    $('#formModalContent').innerHTML = petFormMarkup(pet || {});
    petModal.close();
    formModal.showModal();
  }

  function safeImageUrl(value) {
    if (!value) return photos.tobias;

    try {
      const url = new URL(value);

      return ['https:', 'http:'].includes(url.protocol)
          ? url.href
          : photos.tobias;
    } catch {
      return photos.tobias;
    }
  }

  function adoptionForm(pet) {
    $('#formModalContent').innerHTML = `
      <div class="form-content">
        <p class="eyebrow">Um primeiro oi</p>

        <h2 id="formModalTitle">
          Quero conhecer ${escapeHtml(pet.name)}
        </h2>

        <p>
          Conte como podemos entrar em contato. Este formulário é demonstrativo e salva os dados somente neste navegador.
        </p>

        <form id="interestForm" data-pet-id="${escapeHtml(pet.id)}">
          <div class="form-grid">

            <label class="form-field full">
              <span>Seu nome *</span>
              <input
                name="name"
                required
                maxlength="80"
                autocomplete="name"
                placeholder="Como podemos te chamar?"
              >
            </label>

            <label class="form-field full">
              <span>Seu e-mail *</span>
              <input
                name="email"
                type="email"
                required
                maxlength="120"
                autocomplete="email"
                placeholder="voce@email.com"
              >
            </label>

            <label class="form-field full">
              <span>Sua cidade *</span>
              <input
                name="city"
                required
                maxlength="60"
                placeholder="Cidade onde mora"
              >
            </label>

            <label class="form-field full">
              <span>Uma mensagem para a ONG</span>
              <textarea
                name="message"
                maxlength="400"
                placeholder="Conte um pouquinho sobre você e seu futuro companheiro"
              ></textarea>
            </label>

          </div>

          <button class="button button-orange" type="submit">
            Enviar meu interesse
            <span aria-hidden="true">→</span>
          </button>

          <p class="form-hint">
            Nenhum e-mail será enviado no protótipo.
          </p>
        </form>
      </div>
    `;

    petModal.close();
    formModal.showModal();
  }

  petGrid.addEventListener('click', event => {
    const favoriteButton = event.target.closest('[data-favorite]');

    if (favoriteButton) {
      const id = favoriteButton.dataset.favorite;

      favorites = favorites.includes(id)
          ? favorites.filter(item => item !== id)
          : [...favorites, id];

      localStorage.setItem(
          STORAGE.favorites,
          JSON.stringify(favorites)
      );

      renderPets();
      return;
    }

    const details = event.target.closest('[data-details]');

    if (details) {
      openPet(details.dataset.details);
    }
  });

  // Ações de edição, exclusão e abertura de formulários.
  document.addEventListener('click', async event => {
    const featured = event.target.closest('[data-pet]');

    if (featured) {
      openPet(featured.dataset.pet);
    }

    const adopt = event.target.closest('[data-adopt]');

    if (adopt) {
      const pet = pets.find(item => item.id === adopt.dataset.adopt);

      if (pet) adoptionForm(pet);
    }

    const close = event.target.closest('[data-close]');

    if (close) {
      close.closest('dialog').close();
    }

    const edit = event.target.closest('[data-edit]');

    if (edit) {
      const pet = pets.find(item => item.id === edit.dataset.edit);

      if (pet) openPetForm(pet);
    }

    const remove = event.target.closest('[data-delete]');

    if (remove) {
      const id = remove.dataset.delete;

      try {
        await apiRequest(`${API_URL}/${id}`, {
          method: 'DELETE'
        });

        pets = pets.filter(pet => pet.id !== id);
        favorites = favorites.filter(item => item !== id);

        localStorage.setItem(
            STORAGE.favorites,
            JSON.stringify(favorites)
        );

        petModal.close();
        populateCities();
        renderPets();

        showToast('Pet removido com sucesso!');

      } catch (error) {
        console.error('Erro ao excluir pet:', error);
        showToast('Não foi possível excluir o pet.');
      }
    }
  });

  [petModal, formModal].forEach(dialog => {
    dialog.addEventListener('click', event => {
      if (event.target === dialog) {
        dialog.close();
      }
    });
  });

  $$('.filter-tab').forEach(button => {
    button.addEventListener('click', () => {
      state.species = button.dataset.species;
      state.visibleCount = 8;

      $$('.filter-tab').forEach(tab => {
        tab.classList.toggle('selected', tab === button);
      });

      renderPets();
    });
  });

  ['nameSearch', 'sizeFilter', 'ageFilter', 'cityFilter'].forEach(id => {
    $(`#${id}`).addEventListener('input', () => {
      state.visibleCount = 8;
      renderPets();
    });
  });

  $('#heroSearch').addEventListener('submit', event => {
    event.preventDefault();

    state.location = $('#locationSearch').value.trim();

    $('#nameSearch').value = '';
    $('#cityFilter').value = '';

    $('#encontrar').scrollIntoView({
      behavior: 'smooth'
    });

    renderPets();
  });

  $('#clearFilters').addEventListener('click', () => resetFilters());
  $('#emptyReset').addEventListener('click', () => resetFilters());

  function resetFilters() {
    state.species = 'Todos';
    state.onlyFavorites = false;
    state.location = '';
    state.visibleCount = 8;

    $('#nameSearch').value = '';
    $('#locationSearch').value = '';
    $('#sizeFilter').value = '';
    $('#ageFilter').value = '';
    $('#cityFilter').value = '';

    $$('.filter-tab').forEach(tab => {
      tab.classList.toggle(
          'selected',
          tab.dataset.species === 'Todos'
      );
    });

    renderPets();
  }

  $('#favoritesOnly').addEventListener('click', () => {
    state.onlyFavorites = !state.onlyFavorites;
    renderPets();
  });

  $('#showFavorites').addEventListener('click', () => {
    state.onlyFavorites = !state.onlyFavorites;

    $('#encontrar').scrollIntoView({
      behavior: 'smooth'
    });

    renderPets();
  });

  $('#loadMore').addEventListener('click', () => {
    state.visibleCount += 8;
    renderPets();
  });

  [
    'openPetForm',
    'ctaPetForm',
    'footerPetForm',
    'navPetForm'
  ].forEach(id => {
    $(`#${id}`).addEventListener('click', () => {
      $('#mainNav').classList.remove('open');

      $('#menuToggle').setAttribute(
          'aria-expanded',
          'false'
      );

      openPetForm();
    });
  });

  // Cadastro, edição e formulário de interesse.
  $('#formModalContent').addEventListener('submit', async event => {

    // Cadastro ou edição de um pet.
    if (event.target.id === 'petForm') {
      event.preventDefault();

      const form = event.target;

      if (!form.reportValidity()) return;

      const data = new FormData(form);
      const existingId = form.dataset.petId;

      const nome = data.get('nome').trim();
      const especie = data.get('especie');
      const idade = Number.parseInt(data.get('idade'), 10);

      const cidade = data.get('cidade').trim();
      const estado = data.get('estado').trim().toUpperCase();

      const caracteristicas = data.get('caracteristicas')
          .split(',')
          .map(item => item.trim())
          .filter(item => item.length > 0);

      // Estrutura esperada pelo PetController.java.
      const dadosJava = {
        nome: nome,
        especie: especie,
        idade: idade,
        cidade: cidade,
        estado: estado,
        caracteristicas: caracteristicas
      };

      try {
        let petSalvo;

        if (existingId) {
          // Atualiza o pet na API.
          petSalvo = await apiRequest(`${API_URL}/${existingId}`, {
            method: 'PUT',
            body: JSON.stringify(dadosJava)
          });

          pets = pets.map(item =>
              item.id === existingId
                  ? converterPet(petSalvo)
                  : item
          );

        } else {
          // Cadastra o pet na API.
          petSalvo = await apiRequest(API_URL, {
            method: 'POST',
            body: JSON.stringify(dadosJava)
          });

          pets.unshift(converterPet(petSalvo));
        }

        populateCities();

        formModal.close();
        resetFilters();

        showToast(
            existingId
                ? 'Cadastro atualizado com carinho!'
                : `${nome} agora está no MatchPet!`
        );

      } catch (error) {
        console.error('Erro ao salvar pet:', error);
        showToast('Não foi possível salvar. Verifique o servidor e o banco de dados.');
      }

      return;
    }

    // Formulário de interesse na adoção.
    if (event.target.id === 'interestForm') {
      event.preventDefault();

      const form = event.target;

      if (!form.reportValidity()) return;

      const data = new FormData(form);

      const request = {
        id: `request-${Date.now()}`,
        petId: form.dataset.petId,
        name: data.get('name').trim(),
        email: data.get('email').trim(),
        city: data.get('city').trim(),
        message: data.get('message').trim(),
        date: new Date().toISOString()
      };

      requests.push(request);

      localStorage.setItem(
          STORAGE.requests,
          JSON.stringify(requests)
      );

      formModal.close();

      showToast('Seu interesse foi registrado!');
    }
  });

  // Inicialização da página.
  populateCities();
  renderPets();
  carregarPets();

})();
