import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { empresaPuestos, empresaDesactivar } from '../../api/api'

export default function MisPuestos() {
  const [puestos, setPuestos] = useState([])
  const [confirmarId, setConfirmarId] = useState(null)
  const nav = useNavigate()

  const cargar = () => empresaPuestos().then(setPuestos).catch(() => {})
  useEffect(() => { cargar() }, [])

  const desactivar = async (id) => {
    await empresaDesactivar(id)
    setConfirmarId(null)
    cargar()
  }

  return (
      <div className="container">
        <div className="top-bar">
          <h2>Mis Puestos</h2>
          <button className="btn btn-primary" onClick={() => nav('/empresa/puestos/nuevo')}> Publicar nuevo puesto</button>
        </div>
        {puestos.length === 0
            ? <p className="empty">No has publicado puestos aún.</p>
            : <table>
              <thead><tr><th>ID</th><th>Descripción</th><th>Salario</th><th>Moneda</th><th>Tipo</th><th>Activo</th><th>Acciones</th></tr></thead>
              <tbody>
              {puestos.map(p => (
                  <tr key={p.id}>
                    <td>{p.id}</td>
                    <td>{p.descripcion.substring(0, 55)}{p.descripcion.length > 55 ? '…' : ''}</td>
                    <td>{Number(p.salario).toLocaleString('es-CR')}</td>
                    <td>{p.moneda.codigo}</td>
                    <td>{p.tipo}</td>
                    <td><span className={`badge ${p.activo ? 'badge-activo' : 'badge-inactivo'}`}>{p.activo ? 'Sí' : 'No'}</span></td>
                    <td>
                      {p.activo && (
                          confirmarId === p.id
                              ? <>
                                <span style={{ fontSize: 13, color: 'var(--text-secondary)', marginRight: 8 }}>¿Desactivar?</span>
                                <button className="btn btn-primary btn-sm" onClick={() => desactivar(p.id)}>Sí</button>
                                <button className="btn btn-outline btn-sm" onClick={() => setConfirmarId(null)}>No</button>
                              </>
                              : <button className="btn btn-secondary btn-sm" onClick={() => setConfirmarId(p.id)}>Desactivar</button>
                      )}
                      <button className="btn btn-primary btn-sm" onClick={() => nav(`/empresa/puestos/${p.id}/candidatos`)}>Buscar candidatos</button>
                    </td>
                  </tr>
              ))}
              </tbody>
            </table>
        }
      </div>
  )
}