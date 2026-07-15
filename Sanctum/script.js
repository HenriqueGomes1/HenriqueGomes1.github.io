/* ==========================================================
   GRIMÓRIO — script.js
   Organizador de RPG (Jogador + Mestre)
   Dados persistidos via localStorage.
   ========================================================== */

const STORAGE = {
  players: 'grimorio_players',
  currentPlayer: 'grimorio_current_player',
  npcs: 'grimorio_npcs',
  monsters: 'grimorio_monsters',
};

function loadJSON(key, fallback){
  try{
    const raw = localStorage.getItem(key);
    return raw ? JSON.parse(raw) : fallback;
  }catch(e){ return fallback; }
}
function saveJSON(key, value){
  localStorage.setItem(key, JSON.stringify(value));
}
function uid(){ return Date.now().toString(36) + Math.random().toString(36).slice(2,7); }
function mod(score){ return Math.floor((score - 10) / 2); }
function fmtMod(m){ return (m >= 0 ? '+' : '') + m; }
function profBonusForLevel(level){
  if(level >= 17) return 6;
  if(level >= 13) return 5;
  if(level >= 9) return 4;
  if(level >= 5) return 3;
  return 2;
}

/* ==========================================================
   VIEW ROUTING
   ========================================================== */
const views = {
  home: document.getElementById('view-home'),
  player: document.getElementById('view-player'),
  master: document.getElementById('view-master'),
};
function goto(name){
  Object.values(views).forEach(v => v.classList.remove('active'));
  views[name].classList.add('active');
  window.scrollTo({top:0, behavior:'instant' in window ? 'instant' : 'auto'});
}
document.querySelectorAll('[data-goto]').forEach(btn=>{
  btn.addEventListener('click', ()=> goto(btn.dataset.goto));
});

/* ==========================================================
   ABILITIES / SAVES / SKILLS CONFIG
   ========================================================== */
const ABILITIES = [
  {key:'str', label:'FOR', name:'Força'},
  {key:'dex', label:'DES', name:'Destreza'},
  {key:'con', label:'CON', name:'Constituição'},
  {key:'int', label:'INT', name:'Inteligência'},
  {key:'wis', label:'SAB', name:'Sabedoria'},
  {key:'cha', label:'CAR', name:'Carisma'},
];

const SKILLS = [
  {key:'acrobatics', label:'Acrobacia', attr:'dex'},
  {key:'animal', label:'Adestrar Animais', attr:'wis'},
  {key:'arcana', label:'Arcanismo', attr:'int'},
  {key:'athletics', label:'Atletismo', attr:'str'},
  {key:'deception', label:'Enganação', attr:'cha'},
  {key:'history', label:'História', attr:'int'},
  {key:'insight', label:'Intuição', attr:'wis'},
  {key:'intimidation', label:'Intimidação', attr:'cha'},
  {key:'investigation', label:'Investigação', attr:'int'},
  {key:'medicine', label:'Medicina', attr:'wis'},
  {key:'nature', label:'Natureza', attr:'int'},
  {key:'perception', label:'Percepção', attr:'wis'},
  {key:'performance', label:'Atuação', attr:'cha'},
  {key:'persuasion', label:'Persuasão', attr:'cha'},
  {key:'religion', label:'Religião', attr:'int'},
  {key:'sleight', label:'Prestidigitação', attr:'dex'},
  {key:'stealth', label:'Furtividade', attr:'dex'},
  {key:'survival', label:'Sobrevivência', attr:'wis'},
];

function defaultPlayer(){
  const abilities = {};
  ABILITIES.forEach(a => abilities[a.key] = 10);
  const savesProf = {};
  ABILITIES.forEach(a => savesProf[a.key] = false);
  const skillsProf = {};
  SKILLS.forEach(s => skillsProf[s.key] = false);
  return {
    id: uid(),
    name: '', class: '', level: 1, species: '', background: '', alignment: '',
    ac: 10, init: 0, speed: '9m',
    hpCurrent: 10, hpMax: 10, hitdice: '',
    inspiration: false,
    abilities, savesProf, skillsProf,
    equipment: '', traits: '',
    ptraits: '', ideals: '', bonds: '', flaws: '',
  };
}

/* ==========================================================
   PLAYER STATE
   ========================================================== */
let players = loadJSON(STORAGE.players, []);
let currentPlayerId = localStorage.getItem(STORAGE.currentPlayer) || null;

function getCurrentPlayer(){
  return players.find(p => p.id === currentPlayerId) || null;
}
function persistPlayers(){
  saveJSON(STORAGE.players, players);
  if(currentPlayerId) localStorage.setItem(STORAGE.currentPlayer, currentPlayerId);
}
function refreshPlayerSelect(){
  const sel = document.getElementById('player-select');
  sel.innerHTML = '<option value="">— nova ficha —</option>';
  players.forEach(p=>{
    const opt = document.createElement('option');
    opt.value = p.id;
    opt.textContent = p.name || '(sem nome)';
    if(p.id === currentPlayerId) opt.selected = true;
    sel.appendChild(opt);
  });
}

/* Build abilities UI (used for both player sheet render, structure fixed in HTML already) */
function buildAbilitiesGrid(){
  const grid = document.getElementById('abilities-grid');
  grid.innerHTML = '';
  ABILITIES.forEach(a=>{
    const box = document.createElement('div');
    box.className = 'ability-box';
    box.innerHTML = `
      <span class="ab-name">${a.label}</span>
      <input type="number" id="ab-${a.key}" min="1" max="30" value="10">
      <span class="ab-mod" id="ab-mod-${a.key}">+0</span>
    `;
    grid.appendChild(box);
  });
}
function buildSavesGrid(){
  const grid = document.getElementById('saves-grid');
  grid.innerHTML = '';
  ABILITIES.forEach(a=>{
    const row = document.createElement('label');
    row.className = 'save-row';
    row.innerHTML = `
      <input type="checkbox" id="save-${a.key}">
      <span class="row-label">${a.name}</span>
      <span class="row-mod" id="save-mod-${a.key}">+0</span>
    `;
    grid.appendChild(row);
  });
}
function buildSkillsGrid(){
  const grid = document.getElementById('skills-grid');
  grid.innerHTML = '';
  SKILLS.forEach(s=>{
    const row = document.createElement('label');
    row.className = 'skill-row';
    row.innerHTML = `
      <input type="checkbox" id="skill-${s.key}">
      <span class="row-attr">${s.attr.toUpperCase()}</span>
      <span class="row-label">${s.label}</span>
      <span class="row-mod" id="skill-mod-${s.key}">+0</span>
    `;
    grid.appendChild(row);
  });
}
buildAbilitiesGrid();
buildSavesGrid();
buildSkillsGrid();

/* Recompute all derived numbers (mods, saves, skills, prof bonus) */
function recomputeDerived(){
  const level = parseInt(document.getElementById('p-level').value) || 1;
  const prof = profBonusForLevel(level);
  document.getElementById('p-prof').value = fmtMod(prof);

  const abilityMods = {};
  ABILITIES.forEach(a=>{
    const score = parseInt(document.getElementById(`ab-${a.key}`).value) || 10;
    const m = mod(score);
    abilityMods[a.key] = m;
    document.getElementById(`ab-mod-${a.key}`).textContent = fmtMod(m);
  });

  ABILITIES.forEach(a=>{
    const isProf = document.getElementById(`save-${a.key}`).checked;
    const total = abilityMods[a.key] + (isProf ? prof : 0);
    document.getElementById(`save-mod-${a.key}`).textContent = fmtMod(total);
  });

  SKILLS.forEach(s=>{
    const isProf = document.getElementById(`skill-${s.key}`).checked;
    const total = abilityMods[s.attr] + (isProf ? prof : 0);
    document.getElementById(`skill-mod-${s.key}`).textContent = fmtMod(total);
  });

  // DEX-based initiative default suggestion (doesn't override manual edits forcibly on load)
}

/* Serialize current form into a player object */
function readFormToPlayer(base){
  const p = Object.assign({}, base);
  p.name = document.getElementById('p-name').value;
  p.class = document.getElementById('p-class').value;
  p.level = parseInt(document.getElementById('p-level').value) || 1;
  p.species = document.getElementById('p-species').value;
  p.background = document.getElementById('p-background').value;
  p.alignment = document.getElementById('p-alignment').value;
  p.ac = parseInt(document.getElementById('p-ac').value) || 0;
  p.init = parseInt(document.getElementById('p-init').value) || 0;
  p.speed = document.getElementById('p-speed').value;
  p.hpCurrent = parseInt(document.getElementById('p-hp-current').value) || 0;
  p.hpMax = parseInt(document.getElementById('p-hp-max').value) || 0;
  p.hitdice = document.getElementById('p-hitdice').value;
  p.inspiration = document.getElementById('p-inspiration').checked;

  p.abilities = {};
  ABILITIES.forEach(a=> p.abilities[a.key] = parseInt(document.getElementById(`ab-${a.key}`).value) || 10);
  p.savesProf = {};
  ABILITIES.forEach(a=> p.savesProf[a.key] = document.getElementById(`save-${a.key}`).checked);
  p.skillsProf = {};
  SKILLS.forEach(s=> p.skillsProf[s.key] = document.getElementById(`skill-${s.key}`).checked);

  p.equipment = document.getElementById('p-equipment').value;
  p.traits = document.getElementById('p-traits').value;
  p.ptraits = document.getElementById('p-ptraits').value;
  p.ideals = document.getElementById('p-ideals').value;
  p.bonds = document.getElementById('p-bonds').value;
  p.flaws = document.getElementById('p-flaws').value;
  return p;
}

/* Paint a player object into the form */
function renderPlayerToForm(p){
  document.getElementById('p-name').value = p.name || '';
  document.getElementById('p-class').value = p.class || '';
  document.getElementById('p-level').value = p.level || 1;
  document.getElementById('p-species').value = p.species || '';
  document.getElementById('p-background').value = p.background || '';
  document.getElementById('p-alignment').value = p.alignment || '';
  document.getElementById('p-ac').value = p.ac ?? 10;
  document.getElementById('p-init').value = p.init ?? 0;
  document.getElementById('p-speed').value = p.speed || '9m';
  document.getElementById('p-hp-current').value = p.hpCurrent ?? 10;
  document.getElementById('p-hp-max').value = p.hpMax ?? 10;
  document.getElementById('p-hitdice').value = p.hitdice || '';
  document.getElementById('p-inspiration').checked = !!p.inspiration;

  ABILITIES.forEach(a=> document.getElementById(`ab-${a.key}`).value = p.abilities?.[a.key] ?? 10);
  ABILITIES.forEach(a=> document.getElementById(`save-${a.key}`).checked = !!p.savesProf?.[a.key]);
  SKILLS.forEach(s=> document.getElementById(`skill-${s.key}`).checked = !!p.skillsProf?.[s.key]);

  document.getElementById('p-equipment').value = p.equipment || '';
  document.getElementById('p-traits').value = p.traits || '';
  document.getElementById('p-ptraits').value = p.ptraits || '';
  document.getElementById('p-ideals').value = p.ideals || '';
  document.getElementById('p-bonds').value = p.bonds || '';
  document.getElementById('p-flaws').value = p.flaws || '';

  recomputeDerived();
}

function loadOrCreateCurrentPlayer(){
  let p = getCurrentPlayer();
  if(!p){
    p = defaultPlayer();
    players.push(p);
    currentPlayerId = p.id;
    persistPlayers();
  }
  renderPlayerToForm(p);
  refreshPlayerSelect();
}

let saveTimer = null;
function scheduleAutosave(){
  clearTimeout(saveTimer);
  saveTimer = setTimeout(()=>{
    let p = getCurrentPlayer();
    if(!p) return;
    const idx = players.findIndex(pl => pl.id === p.id);
    players[idx] = readFormToPlayer(p);
    persistPlayers();
    refreshPlayerSelect();
    const ind = document.getElementById('player-save-indicator');
    ind.classList.add('show');
    setTimeout(()=> ind.classList.remove('show'), 1200);
  }, 500);
}

document.getElementById('view-player').addEventListener('input', (e)=>{
  recomputeDerived();
  scheduleAutosave();
});

document.getElementById('player-select').addEventListener('change', (e)=>{
  const id = e.target.value;
  if(!id){
    const p = defaultPlayer();
    players.push(p);
    currentPlayerId = p.id;
    persistPlayers();
    renderPlayerToForm(p);
    refreshPlayerSelect();
    return;
  }
  currentPlayerId = id;
  persistPlayers();
  renderPlayerToForm(getCurrentPlayer());
});

document.getElementById('player-new').addEventListener('click', ()=>{
  const p = defaultPlayer();
  players.push(p);
  currentPlayerId = p.id;
  persistPlayers();
  renderPlayerToForm(p);
  refreshPlayerSelect();
  document.getElementById('p-name').focus();
});

document.getElementById('player-delete').addEventListener('click', ()=>{
  if(!currentPlayerId) return;
  const p = getCurrentPlayer();
  if(!p) return;
  if(!confirm(`Excluir a ficha de "${p.name || '(sem nome)'}"? Essa ação não pode ser desfeita.`)) return;
  players = players.filter(pl => pl.id !== currentPlayerId);
  currentPlayerId = players.length ? players[0].id : null;
  persistPlayers();
  if(!currentPlayerId){
    loadOrCreateCurrentPlayer();
  }else{
    renderPlayerToForm(getCurrentPlayer());
    refreshPlayerSelect();
  }
});

/* ==========================================================
   MASTER: NPCs & MONSTERS
   ========================================================== */
let npcs = loadJSON(STORAGE.npcs, []);
let monsters = loadJSON(STORAGE.monsters, []);
let editingNpcId = null;
let editingMonsterId = null;

document.querySelectorAll('.tab-btn').forEach(btn=>{
  btn.addEventListener('click', ()=>{
    document.querySelectorAll('.tab-btn').forEach(b=> b.classList.remove('active'));
    btn.classList.add('active');
    const tab = btn.dataset.tab;
    document.getElementById('panel-npc').hidden = tab !== 'npc';
    document.getElementById('panel-monster').hidden = tab !== 'monster';
  });
});

/* --- NPCs --- */
function clearNpcForm(){
  editingNpcId = null;
  ['npc-name','npc-race','npc-location','npc-appearance','npc-personality','npc-notes'].forEach(id=>{
    document.getElementById(id).value = '';
  });
  document.getElementById('npc-attitude').value = 'Amigável';
}
function renderNpcList(){
  const list = document.getElementById('npc-list');
  const q = document.getElementById('npc-search').value.trim().toLowerCase();
  const filtered = npcs.filter(n => !q || (n.name||'').toLowerCase().includes(q) || (n.race||'').toLowerCase().includes(q));
  list.innerHTML = '';
  if(!filtered.length){
    list.innerHTML = '<div class="empty-state">Nenhum NPC ainda. Crie o primeiro ao lado ➜</div>';
    return;
  }
  filtered.slice().reverse().forEach(n=>{
    const card = document.createElement('div');
    card.className = 'entity-card';
    card.innerHTML = `
      <button class="entity-remove" title="Excluir" data-id="${n.id}">✕</button>
      <span class="entity-tag">${n.attitude || 'Neutro'}</span>
      <h4>${escapeHTML(n.name || '(sem nome)')}</h4>
      <p>${escapeHTML(n.race || '')}${n.location ? ' · ' + escapeHTML(n.location) : ''}</p>
    `;
    card.addEventListener('click', (e)=>{
      if(e.target.classList.contains('entity-remove')) return;
      openNpcModal(n);
    });
    card.querySelector('.entity-remove').addEventListener('click', (e)=>{
      e.stopPropagation();
      if(!confirm(`Excluir "${n.name || 'este NPC'}"?`)) return;
      npcs = npcs.filter(x => x.id !== n.id);
      saveJSON(STORAGE.npcs, npcs);
      renderNpcList();
    });
    list.appendChild(card);
  });
}
function openNpcModal(n){
  document.getElementById('modal-body').innerHTML = `
    <h2>${escapeHTML(n.name || '(sem nome)')}</h2>
    <div class="m-meta">${escapeHTML(n.race || '')}${n.location ? ' · ' + escapeHTML(n.location) : ''} · ${escapeHTML(n.attitude || '')}</div>
    ${n.appearance ? `<h4>Aparência</h4><p>${escapeHTML(n.appearance)}</p>` : ''}
    ${n.personality ? `<h4>Personalidade &amp; Motivações</h4><p>${escapeHTML(n.personality)}</p>` : ''}
    ${n.notes ? `<h4>Notas do Mestre</h4><p>${escapeHTML(n.notes)}</p>` : ''}
    <div class="form-actions" style="margin-top:1.2rem;">
      <button class="btn-primary btn-master" id="modal-edit-npc">Editar</button>
    </div>
  `;
  document.getElementById('modal-edit-npc').addEventListener('click', ()=>{
    editingNpcId = n.id;
    document.getElementById('npc-name').value = n.name || '';
    document.getElementById('npc-race').value = n.race || '';
    document.getElementById('npc-location').value = n.location || '';
    document.getElementById('npc-attitude').value = n.attitude || 'Amigável';
    document.getElementById('npc-appearance').value = n.appearance || '';
    document.getElementById('npc-personality').value = n.personality || '';
    document.getElementById('npc-notes').value = n.notes || '';
    closeModal();
    document.getElementById('npc-name').scrollIntoView({behavior:'smooth', block:'center'});
  });
  openModal();
}
document.getElementById('npc-save').addEventListener('click', ()=>{
  const name = document.getElementById('npc-name').value.trim();
  if(!name){ alert('Dê um nome ao NPC antes de salvar.'); return; }
  const data = {
    id: editingNpcId || uid(),
    name,
    race: document.getElementById('npc-race').value,
    location: document.getElementById('npc-location').value,
    attitude: document.getElementById('npc-attitude').value,
    appearance: document.getElementById('npc-appearance').value,
    personality: document.getElementById('npc-personality').value,
    notes: document.getElementById('npc-notes').value,
  };
  if(editingNpcId){
    const idx = npcs.findIndex(n => n.id === editingNpcId);
    npcs[idx] = data;
  }else{
    npcs.push(data);
  }
  saveJSON(STORAGE.npcs, npcs);
  clearNpcForm();
  renderNpcList();
});
document.getElementById('npc-clear').addEventListener('click', clearNpcForm);
document.getElementById('npc-search').addEventListener('input', renderNpcList);

/* --- Monsters --- */
function buildMonAbilitiesGrid(){
  const grid = document.getElementById('mon-abilities-grid');
  grid.innerHTML = '';
  ABILITIES.forEach(a=>{
    const box = document.createElement('div');
    box.className = 'ability-box';
    box.innerHTML = `
      <span class="ab-name">${a.label}</span>
      <input type="number" id="mon-ab-${a.key}" min="1" max="30" value="10">
    `;
    grid.appendChild(box);
  });
}
buildMonAbilitiesGrid();

function clearMonForm(){
  editingMonsterId = null;
  ['mon-name','mon-type','mon-cr','mon-hp','mon-actions','mon-traits','mon-notes'].forEach(id=>{
    document.getElementById(id).value = '';
  });
  document.getElementById('mon-ac').value = 12;
  document.getElementById('mon-speed').value = '12m';
  ABILITIES.forEach(a=> document.getElementById(`mon-ab-${a.key}`).value = 10);
}
function renderMonList(){
  const list = document.getElementById('mon-list');
  const q = document.getElementById('mon-search').value.trim().toLowerCase();
  const filtered = monsters.filter(m => !q || (m.name||'').toLowerCase().includes(q) || (m.type||'').toLowerCase().includes(q));
  list.innerHTML = '';
  if(!filtered.length){
    list.innerHTML = '<div class="empty-state">Nenhum monstro ainda. Crie o primeiro ao lado ➜</div>';
    return;
  }
  filtered.slice().reverse().forEach(m=>{
    const card = document.createElement('div');
    card.className = 'entity-card';
    card.innerHTML = `
      <button class="entity-remove" title="Excluir" data-id="${m.id}">✕</button>
      <span class="entity-tag">ND ${escapeHTML(m.cr || '?')}</span>
      <h4>${escapeHTML(m.name || '(sem nome)')}</h4>
      <p>${escapeHTML(m.type || '')} · CA ${escapeHTML(String(m.ac ?? '—'))}</p>
    `;
    card.addEventListener('click', (e)=>{
      if(e.target.classList.contains('entity-remove')) return;
      openMonModal(m);
    });
    card.querySelector('.entity-remove').addEventListener('click', (e)=>{
      e.stopPropagation();
      if(!confirm(`Excluir "${m.name || 'este monstro'}"?`)) return;
      monsters = monsters.filter(x => x.id !== m.id);
      saveJSON(STORAGE.monsters, monsters);
      renderMonList();
    });
    list.appendChild(card);
  });
}
function openMonModal(m){
  const abLine = ABILITIES.map(a => `${a.label} ${m.abilities?.[a.key] ?? 10} (${fmtMod(mod(m.abilities?.[a.key] ?? 10))})`).join(' · ');
  document.getElementById('modal-body').innerHTML = `
    <h2>${escapeHTML(m.name || '(sem nome)')}</h2>
    <div class="m-meta">${escapeHTML(m.type || '')} · ND ${escapeHTML(m.cr || '?')} · CA ${escapeHTML(String(m.ac ?? '—'))} · PV ${escapeHTML(m.hp || '—')} · Desl. ${escapeHTML(m.speed || '—')}</div>
    <h4>Atributos</h4><p>${escapeHTML(abLine)}</p>
    ${m.traits ? `<h4>Traços Especiais</h4><p>${escapeHTML(m.traits)}</p>` : ''}
    ${m.actions ? `<h4>Ações</h4><p>${escapeHTML(m.actions)}</p>` : ''}
    ${m.notes ? `<h4>Notas do Mestre</h4><p>${escapeHTML(m.notes)}</p>` : ''}
    <div class="form-actions" style="margin-top:1.2rem;">
      <button class="btn-primary btn-master" id="modal-edit-mon">Editar</button>
    </div>
  `;
  document.getElementById('modal-edit-mon').addEventListener('click', ()=>{
    editingMonsterId = m.id;
    document.getElementById('mon-name').value = m.name || '';
    document.getElementById('mon-type').value = m.type || '';
    document.getElementById('mon-cr').value = m.cr || '';
    document.getElementById('mon-ac').value = m.ac ?? 12;
    document.getElementById('mon-hp').value = m.hp || '';
    document.getElementById('mon-speed').value = m.speed || '12m';
    ABILITIES.forEach(a=> document.getElementById(`mon-ab-${a.key}`).value = m.abilities?.[a.key] ?? 10);
    document.getElementById('mon-actions').value = m.actions || '';
    document.getElementById('mon-traits').value = m.traits || '';
    document.getElementById('mon-notes').value = m.notes || '';
    closeModal();
    document.getElementById('mon-name').scrollIntoView({behavior:'smooth', block:'center'});
  });
  openModal();
}
document.getElementById('mon-save').addEventListener('click', ()=>{
  const name = document.getElementById('mon-name').value.trim();
  if(!name){ alert('Dê um nome ao monstro antes de salvar.'); return; }
  const abilities = {};
  ABILITIES.forEach(a=> abilities[a.key] = parseInt(document.getElementById(`mon-ab-${a.key}`).value) || 10);
  const data = {
    id: editingMonsterId || uid(),
    name,
    type: document.getElementById('mon-type').value,
    cr: document.getElementById('mon-cr').value,
    ac: parseInt(document.getElementById('mon-ac').value) || 10,
    hp: document.getElementById('mon-hp').value,
    speed: document.getElementById('mon-speed').value,
    abilities,
    actions: document.getElementById('mon-actions').value,
    traits: document.getElementById('mon-traits').value,
    notes: document.getElementById('mon-notes').value,
  };
  if(editingMonsterId){
    const idx = monsters.findIndex(m => m.id === editingMonsterId);
    monsters[idx] = data;
  }else{
    monsters.push(data);
  }
  saveJSON(STORAGE.monsters, monsters);
  clearMonForm();
  renderMonList();
});
document.getElementById('mon-clear').addEventListener('click', clearMonForm);
document.getElementById('mon-search').addEventListener('input', renderMonList);

/* --- Modal helpers --- */
function openModal(){ document.getElementById('detail-modal').hidden = false; }
function closeModal(){ document.getElementById('detail-modal').hidden = true; }
document.getElementById('modal-close').addEventListener('click', closeModal);
document.getElementById('detail-modal').addEventListener('click', (e)=>{
  if(e.target.id === 'detail-modal') closeModal();
});
document.addEventListener('keydown', (e)=>{
  if(e.key === 'Escape') closeModal();
});

function escapeHTML(str){
  const div = document.createElement('div');
  div.textContent = str ?? '';
  return div.innerHTML;
}

/* ==========================================================
   INIT
   ========================================================== */
loadOrCreateCurrentPlayer();
renderNpcList();
renderMonList();
