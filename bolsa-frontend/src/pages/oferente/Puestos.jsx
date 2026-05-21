import { useEffect, useState } from 'react'
import { getCaracteristicas, oferenteAPI } from '../../api/api'
import { ArbolCheckboxes, BarraCoincidencia } from '../../components/UI'

const estadoColor = { CUMPLE: '#dcfce7', PARCIAL: '#fef3c7', INSUFICIENTE: '#fee2e2', AUSENTE: '#ede9fe' }
const estadoTexto = { CUMPLE: '✓', PARCIAL: '~', INSUFICIENTE: '!', AUSENTE: '✗' }

export default function OferentePuestos() {
  const [raices, setRaices] = useState([])
  const [seleccionadas, setSeleccionadas] = useState([])
  const [modoTodos, setModoTodos] = useState(false)
  const [resultados, setResultados] = useState([])
  const [buscado, setBuscado] = useState(false)

  useEffect(() => { getCaracteristicas().then(setRaices) }, [])

  const buscar = async () => {
    const params = { modoTodos }
    if (seleccionadas.length) params.caracteristicas = seleccionadas
    const res = await oferenteAPI.buscarPuestos(params)
    setResultados(res || [])
    setBuscado(true)
  }

  const limpiar = () => { setSeleccionadas([]); setResultados([]); setBuscado(false) }

  return (
    <div className="container-wide">
      <h2>Buscar Puestos Disponibles</h2>
      <div className="arbol">
        <ArbolCheckboxes raices={raices} seleccionadas={seleccionadas} onChange={setSeleccionadas} />
      </div>
      <div className="modo">
        <strong>Modo:</strong>
        <label><input type="radio" checked={!modoTodos} onChange={() => setModoTodos(false)} /> Cualquiera (OR)</label>
        <label><input type="radio" checked={modoTodos} onChange={() => setModoTodos(true)} /> Todos (AND)</label>
      </div>
      <div className="btns">
        <button className="btn btn-primary" onClick={buscar}>Buscar</button>
        <button className="btn btn-outline" onClick={limpiar}>Limpiar</button>
      </div>
      <h3>Resultados</h3>
      {buscado && resultados.length === 0 && <p className="empty">No se encontraron puestos.</p>}
      {resultados.map(p => (
        <div key={p.id} className="card" style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <strong style={{ color: '#4c0519' }}>{p.empresa.nombre}</strong>
              <span style={{ fontSize: 12, color: '#9f1239', marginLeft: 8 }}>{p.tipo}</span>
              <div style={{ fontSize: 14, marginTop: 4 }}>{p.descripcion.substring(0, 80)}…</div>
              <div style={{ fontSize: 13, color: '#4c0519', fontWeight: 'bold', marginTop: 6 }}>
                {p.moneda.codigo} {Number(p.salario).toLocaleString('es-CR')}
              </div>
            </div>
            <div style={{ textAlign: 'center', minWidth: 90 }}>
              <div style={{ fontSize: 26, fontWeight: 900, color: p.porcentajeCoincidencia >= 70 ? '#15803d' : p.porcentajeCoincidencia >= 40 ? '#b45309' : '#dc2626' }}>
                {p.porcentajeCoincidencia.toFixed(1)}%
              </div>
              <div style={{ fontSize: 11, color: '#9f1239' }}>match</div>
              <div style={{ fontSize: 11, color: '#6b7280' }}>{p.requisitosCumplidos}/{p.requisitosTotal} cumplidos</div>
            </div>
          </div>
          <div style={{ marginTop: 10 }}><BarraCoincidencia pct={p.porcentajeCoincidencia} /></div>
          {p.coincidencias && p.coincidencias.length > 0 && (
            <div style={{ marginTop: 10, display: 'flex', flexWrap: 'wrap', gap: 4 }}>
              {p.coincidencias.map((d, i) => (
                <span key={i} style={{ fontSize: 11, padding: '2px 8px', borderRadius: 10, background: estadoColor[d.estado], border: '1px solid #d1d5db' }}>
                  {estadoTexto[d.estado]} {d.caracteristica} {d.nivelOferente > 0 ? `(${d.nivelOferente}→${d.nivelRequerido})` : '(sin habilidad)'}
                </span>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  )
}
