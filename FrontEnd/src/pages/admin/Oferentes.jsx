import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { adminOferentesPendientes, adminAprobarOferente, adminRechazarOferente } from '../../api/api'

export default function AdminOferentes() {
  const [pendientes, setPendientes] = useState([])
  const nav = useNavigate()

  const cargar = () => adminOferentesPendientes().then(setPendientes).catch(() => {})
  useEffect(() => { cargar() }, [])

  return (
    <div className="container">
      <div className="top-bar"><h2>Oferentes Pendientes de Aprobación</h2></div>
      {pendientes.length === 0
        ? <p className="empty">No hay oferentes pendientes.</p>
        : <table>
            <thead><tr><th>Nombre</th><th>Identificación</th><th>Correo</th><th>Teléfono</th><th>Nacionalidad</th><th>Acciones</th></tr></thead>
            <tbody>
              {pendientes.map(o => (
                <tr key={o.usuarioId}>
                  <td>{o.nombre} {o.primerApellido}</td><td>{o.identificacion}</td><td>{o.correo}</td><td>{o.telefono}</td><td>{o.nacionalidad}</td>
                  <td>
                    <button className="btn btn-primary btn-sm" onClick={async () => { await adminAprobarOferente(o.usuarioId); cargar() }}> Aprobar</button>
                    <button className="btn btn-secondary btn-sm" onClick={async () => { await adminRechazarOferente(o.usuarioId); cargar() }}> Rechazar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
      }
      <br /><button className="btn btn-outline" onClick={() => nav('/admin/dashboard')}> Dashboard</button>
    </div>
  )
}
