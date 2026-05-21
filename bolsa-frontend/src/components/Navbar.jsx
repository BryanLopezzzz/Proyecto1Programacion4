import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { isLogged, role, user, logout } = useAuth()
  const nav = useNavigate()

  const go = (path) => nav(path)

  const handleLogout = () => { logout(); nav('/') }

  let links = null
  if (!isLogged) {
    links = <>
      <a onClick={() => go('/')}>Inicio</a>
      <a onClick={() => go('/buscar')}>Buscar puestos</a>
      <a onClick={() => go('/registro/empresa')}>Registro Empresa</a>
      <a onClick={() => go('/registro/oferente')}>Registro Oferente</a>
    </>
  } else if (role === 'ROLE_EMPRESA') {
    links = <>
      <a onClick={() => go('/empresa/dashboard')}>Dashboard</a>
      <a onClick={() => go('/empresa/puestos')}>Mis puestos</a>
      <a onClick={() => go('/empresa/puestos/nuevo')}>Publicar puesto</a>
    </>
  } else if (role === 'ROLE_OFERENTE') {
    links = <>
      <a onClick={() => go('/oferente/dashboard')}>Dashboard</a>
      <a onClick={() => go('/oferente/habilidades')}>Mis habilidades</a>
      <a onClick={() => go('/oferente/cv')}>Mi CV</a>
      <a onClick={() => go('/oferente/puestos')}>Buscar puestos</a>
    </>
  } else if (role === 'ROLE_ADMIN') {
    links = <>
      <a onClick={() => go('/admin/dashboard')}>Dashboard</a>
      <a onClick={() => go('/admin/empresas')}>Empresas</a>
      <a onClick={() => go('/admin/oferentes')}>Oferentes</a>
      <a onClick={() => go('/admin/caracteristicas')}>Características</a>
      <a onClick={() => go('/admin/reportes')}>Reportes</a>
    </>
  }

  return (
    <nav>
      <div>
        <span className="brand" onClick={() => go('/')} style={{ cursor: 'pointer' }}>BolsaEmpleo</span>
        {links}
      </div>
      <div>
        {isLogged
          ? <><span style={{ color: '#fecdd3', fontSize: 13 }}>{user}</span>
              <a onClick={handleLogout} style={{ color: '#fecdd3', fontSize: 13, marginLeft: 14, cursor: 'pointer' }}>Salir</a></>
          : <a onClick={() => go('/login')} style={{ color: '#fecdd3', fontSize: 13, cursor: 'pointer' }}>Login</a>
        }
      </div>
    </nav>
  )
}
