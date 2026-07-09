/**
 * app.js — Utilitários compartilhados
 * Injeta a barra de salvar/carregar em todas as páginas internas.
 */

/* ── Dicionários de exibição ──────────────────────────────────── */

const CLASSES_RPG = {
  GUERREIRO:'Guerreiro', MAGO:'Mago', LADINO:'Ladino', CLERIGO:'Clérigo',
  BARBARO:'Bárbaro', BARDO:'Bardo', PALADINO:'Paladino', PATRULHEIRO:'Patrulheiro',
  FEITICEIRO:'Feiticeiro', BRUXO:'Bruxo', MONGE:'Monge', DRUIDA:'Druida'
};

const ATRIBUTOS_EXIBICAO = {
  FORCA:'Força', DESTREZA:'Destreza', CONSTITUICAO:'Constituição',
  INTELIGENCIA:'Inteligência', SABEDORIA:'Sabedoria', CARISMA:'Carisma'
};

const TIPOS_NPC = { ALIADO:'Aliado', NEUTRO:'Neutro', VILAO:'Vilão', MONSTRO:'Monstro' };

/* ── Helpers ─────────────────────────────────────────────────── */

function calcularBonus(v) { return Math.floor((v - 10) / 2); }
function formatarBonus(v) { const b = calcularBonus(v); return (b >= 0 ? '+' : '') + b; }
function getIdDaUrl(param) { return new URLSearchParams(window.location.search).get(param || 'id'); }
function ir(url) { window.location.href = url; }
function confirmar(msg) { return window.confirm(msg); }

function htmlStatGrid(obj, corBonus) {
  const stats = [
    ['Força',obj.forca],['Destreza',obj.destreza],['Constituição',obj.constituicao],
    ['Inteligência',obj.inteligencia],['Sabedoria',obj.sabedoria],['Carisma',obj.carisma]
  ];
  return `<div class="stat-grid">${stats.map(([l,v]) => `
    <div class="stat-bloco">
      <span class="stat-label">${l}</span>
      <span class="stat-valor">${v ?? 10}</span>
      <span class="stat-bonus" style="color:${corBonus}">${formatarBonus(v ?? 10)}</span>
    </div>`).join('')}</div>`;
}

/* ── Toast ───────────────────────────────────────────────────── */

function toast(msg, tipo) {
  const cores = { info:'#1f4d38', erro:'#7f1d1d', sucesso:'#14532d', aviso:'#78350f' };
  const el = document.createElement('div');
  el.className = 'toast';
  el.style.background = cores[tipo] || cores.info;
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(() => { el.style.opacity = '0'; setTimeout(() => el.remove(), 400); }, 3000);
}

/* ── Barra de salvar/carregar ─────────────────────────────────── */

function injetarBarraSalvar() {
  // Não injeta na tela inicial
  if (window.location.pathname.endsWith('index.html') ||
      window.location.pathname === '/' ||
      window.location.pathname.endsWith('/')) return;

  const input = document.createElement('input');
  input.type = 'file';
  input.accept = '.json';
  input.style.display = 'none';
  input.addEventListener('change', e => {
    const arquivo = e.target.files[0];
    if (!arquivo) return;
    importarJSON(arquivo, (err, dados) => {
      if (err) { toast('Erro ao ler o arquivo: ' + err.message, 'erro'); return; }
      const np = (dados.personagens || []).length;
      const nc = (dados.campanhas || []).length;
      const nn = (dados.campanhas || []).reduce((s,c) => s + (c.npcs||[]).length, 0);
      if (!confirmar(`Importar ${np} personagem(ns), ${nc} campanha(s) e ${nn} NPC(s)?\n\nIsso substituirá todos os dados atuais.`)) return;
      aplicarImportacao(dados);
      toast('Dados importados com sucesso!', 'sucesso');
      setTimeout(() => location.reload(), 800);
    });
    input.value = ''; // permite reimportar o mesmo arquivo
  });
  document.body.appendChild(input);

  const barra = document.createElement('div');
  barra.className = 'barra-salvar';
  barra.innerHTML = `
    <span class="barra-salvar-titulo">⚔ RPG Master</span>
    <div class="barra-salvar-acoes">
      <button class="botao botao-sm botao-verde" id="btn-exportar">
        💾 Exportar JSON
      </button>
      <button class="botao botao-sm botao-secundario" id="btn-importar">
        📂 Importar JSON
      </button>
    </div>`;
  document.body.appendChild(barra);

  document.getElementById('btn-exportar').addEventListener('click', () => {
    exportarJSON();
    toast('Arquivo gerado — verifique sua pasta de downloads.', 'sucesso');
  });
  document.getElementById('btn-importar').addEventListener('click', () => input.click());
}

/* ── Init ─────────────────────────────────────────────────────── */

popularDadosDeExemplo();
injetarBarraSalvar();
