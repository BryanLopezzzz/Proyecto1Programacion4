import { useNavigate } from 'react-router-dom'

export default function AdminDashboard() {
  const nav = useNavigate()
  return (
    <div className="container">
      <h1>Panel de Administración</h1>
      <p>Bienvenido al panel de control. Gestioná empresas, oferentes, características y reportes.</p>
      <div className="card-grid">
        <a className="card-btn c1" onClick={() => nav('/admin/empresas')} style={{ cursor: 'pointer' }}>Empresas pendientes</a>
        <a className="card-btn c2" onClick={() => nav('/admin/oferentes')} style={{ cursor: 'pointer' }}>Oferentes pendientes</a>
        <a className="card-btn c3" onClick={() => nav('/admin/caracteristicas')} style={{ cursor: 'pointer' }}>Características</a>
        <a className="card-btn c1" onClick={() => nav('/admin/reportes')} style={{ cursor: 'pointer' }}>Reportes</a>
      </div>
    </div>
  )
}
