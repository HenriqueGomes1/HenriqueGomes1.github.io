/**
 * storage.js — Persistência local (localStorage)
 *
 * Todos os dados ficam no localStorage enquanto você usa o sistema.
 * Para salvar permanentemente, use Exportar JSON (barra inferior).
 * Para restaurar, use Importar JSON.
 */

const CHAVE_PERSONAGENS = 'rpgorg_personagens';
const CHAVE_CAMPANHAS   = 'rpgorg_campanhas';

/* ── IDs ─────────────────────────────────────────────────────── */

function gerarId() {
  return Date.now().toString(36) + Math.random().toString(36).slice(2, 8);
}

/* ── Personagens ──────────────────────────────────────────────── */

function listarPersonagens() {
  return JSON.parse(localStorage.getItem(CHAVE_PERSONAGENS) || '[]');
}

function buscarPersonagem(id) {
  return listarPersonagens().find(p => p.id === id) || null;
}

function salvarPersonagem(dados) {
  const lista = listarPersonagens();
  if (dados.id) {
    const idx = lista.findIndex(p => p.id === dados.id);
    if (idx !== -1) lista[idx] = dados;
    else lista.push(dados);
  } else {
    dados.id = gerarId();
    dados.habilidades = dados.habilidades || [];
    lista.push(dados);
  }
  localStorage.setItem(CHAVE_PERSONAGENS, JSON.stringify(lista));
  return dados;
}

function excluirPersonagem(id) {
  const lista = listarPersonagens().filter(p => p.id !== id);
  localStorage.setItem(CHAVE_PERSONAGENS, JSON.stringify(lista));
  // Remove vínculo em campanhas
  listarCampanhas().forEach(c => {
    if ((c.personagensIds || []).includes(id)) {
      c.personagensIds = c.personagensIds.filter(pid => pid !== id);
      salvarCampanha(c);
    }
  });
}

/* ── Campanhas ────────────────────────────────────────────────── */

function listarCampanhas() {
  return JSON.parse(localStorage.getItem(CHAVE_CAMPANHAS) || '[]');
}

function buscarCampanha(id) {
  return listarCampanhas().find(c => c.id === id) || null;
}

function salvarCampanha(dados) {
  const lista = listarCampanhas();
  if (dados.id) {
    const idx = lista.findIndex(c => c.id === dados.id);
    if (idx !== -1) lista[idx] = dados;
    else lista.push(dados);
  } else {
    dados.id = gerarId();
    dados.dataCriacao = new Date().toLocaleDateString('pt-BR');
    dados.npcs = dados.npcs || [];
    dados.personagensIds = dados.personagensIds || [];
    lista.push(dados);
  }
  localStorage.setItem(CHAVE_CAMPANHAS, JSON.stringify(lista));
  return dados;
}

function excluirCampanha(id) {
  const lista = listarCampanhas().filter(c => c.id !== id);
  localStorage.setItem(CHAVE_CAMPANHAS, JSON.stringify(lista));
}

/* ── NPCs (embutidos na campanha) ─────────────────────────────── */

function salvarNpcNaCampanha(campanhaId, npcDados) {
  const campanha = buscarCampanha(campanhaId);
  if (!campanha) return null;
  campanha.npcs = campanha.npcs || [];
  if (npcDados.id) {
    // edição
    const idx = campanha.npcs.findIndex(n => n.id === npcDados.id);
    if (idx !== -1) campanha.npcs[idx] = npcDados;
    else campanha.npcs.push(npcDados);
  } else {
    npcDados.id = gerarId();
    campanha.npcs.push(npcDados);
  }
  salvarCampanha(campanha);
  return npcDados;
}

function excluirNpcDaCampanha(campanhaId, npcId) {
  const campanha = buscarCampanha(campanhaId);
  if (!campanha) return;
  campanha.npcs = (campanha.npcs || []).filter(n => n.id !== npcId);
  salvarCampanha(campanha);
}

function buscarNpc(campanhaId, npcId) {
  const campanha = buscarCampanha(campanhaId);
  if (!campanha) return null;
  return (campanha.npcs || []).find(n => n.id === npcId) || null;
}

/* ── Exportar / Importar JSON ─────────────────────────────────── */

function exportarJSON() {
  const dados = {
    versao: '1.0',
    exportadoEm: new Date().toISOString(),
    personagens: listarPersonagens(),
    campanhas:   listarCampanhas()
  };
  const blob = new Blob([JSON.stringify(dados, null, 2)], { type: 'application/json' });
  const url  = URL.createObjectURL(blob);
  const a    = document.createElement('a');
  const data = new Date().toISOString().slice(0, 10);
  a.href     = url;
  a.download = `rpgmaster-${data}.json`;
  a.click();
  URL.revokeObjectURL(url);
}

function importarJSON(arquivo, callback) {
  const reader = new FileReader();
  reader.onload = e => {
    try {
      const dados = JSON.parse(e.target.result);
      if (!dados.personagens || !dados.campanhas) {
        throw new Error('Arquivo inválido: campos obrigatórios ausentes.');
      }
      callback(null, dados);
    } catch (err) {
      callback(err, null);
    }
  };
  reader.readAsText(arquivo);
}

function aplicarImportacao(dados) {
  localStorage.setItem(CHAVE_PERSONAGENS, JSON.stringify(dados.personagens || []));
  localStorage.setItem(CHAVE_CAMPANHAS,   JSON.stringify(dados.campanhas   || []));
}

/* ── Seed de dados de exemplo (roda só na primeira visita) ─────── */

function popularDadosDeExemplo() {
  if (listarCampanhas().length > 0) return;

  const g = salvarPersonagem({
    nome: 'Thalrik Punho-de-Ferro', classe: 'GUERREIRO', nivel: 3,
    descricao: 'Ex-soldado da guarda real, de poucas palavras e lealdade inabalável.',
    forca: 17, destreza: 13, constituicao: 16, inteligencia: 9, sabedoria: 11, carisma: 10,
    campanhaId: null,
    habilidades: [{ id: gerarId(), nome: 'Golpe Sísmico', descricao: 'Crava a arma no chão provocando um tremor local.', atributoChave: 'FORCA', tipoEfeito: 'fisico' }]
  });

  const m = salvarPersonagem({
    nome: 'Lyriel Vento-Cinza', classe: 'MAGO', nivel: 3,
    descricao: 'Formada na Academia Arcana de Thessar, troca segurança por conhecimento.',
    forca: 8, destreza: 14, constituicao: 12, inteligencia: 18, sabedoria: 13, carisma: 11,
    campanhaId: null,
    habilidades: [{ id: gerarId(), nome: 'Lança Glacial', descricao: 'Condensa umidade do ar em lança de gelo.', atributoChave: 'INTELIGENCIA', tipoEfeito: 'gelo' }]
  });

  const l = salvarPersonagem({
    nome: 'Sable Três-Facas', classe: 'LADINO', nivel: 3,
    descricao: 'Cresceu nos becos do porto e nunca confiou em ninguém além de si.',
    forca: 11, destreza: 17, constituicao: 13, inteligencia: 12, sabedoria: 14, carisma: 15,
    campanhaId: null,
    habilidades: [{ id: gerarId(), nome: 'Passo nas Sombras', descricao: 'Funde-se com a escuridão para flanquear o inimigo.', atributoChave: 'DESTREZA', tipoEfeito: 'sombrio' }]
  });

  const c = salvarCampanha({
    nome: 'A Maldição de Vaelthorn', mestre: 'Mestre Igor',
    descricao: 'Um reino antigo sucumbe a uma maldição que desperta criaturas das sombras.',
    personagensIds: [g.id, m.id, l.id],
    npcs: [
      { id: gerarId(), nome: 'Sumo Cultista Mordrek', tipo: 'VILAO', nivelDesafio: '4', pontosVida: 58,
        descricao: 'Líder do culto. Fala em enigmas e parece sempre um passo à frente.',
        forca: 10, destreza: 12, constituicao: 13, inteligencia: 16, sabedoria: 15, carisma: 17 },
      { id: gerarId(), nome: 'Batedor Goblin Trit', tipo: 'MONSTRO', nivelDesafio: '1/4', pontosVida: 7,
        descricao: 'Pequeno e covarde, prefere emboscadas a combate direto.',
        forca: 8, destreza: 15, constituicao: 10, inteligencia: 8, sabedoria: 8, carisma: 6 },
      { id: gerarId(), nome: 'Borin Mão-Pesada', tipo: 'ALIADO', nivelDesafio: 'N/A', pontosVida: 20,
        descricao: 'Ferreiro da vila, oferece reparos em troca de notícias da estrada.',
        forca: 15, destreza: 9, constituicao: 14, inteligencia: 10, sabedoria: 12, carisma: 11 }
    ]
  });

  [g, m, l].forEach(p => { p.campanhaId = c.id; salvarPersonagem(p); });
}
