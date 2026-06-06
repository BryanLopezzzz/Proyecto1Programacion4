import Footer from './components/Footer'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Navbar from './components/Navbar'

import Inicio from './pages/publico/Inicio'
import Login from './pages/publico/Login'
import Buscar from './pages/publico/Buscar'
import RegistroEmpresa from './pages/publico/RegistroEmpresa'
import RegistroOferente from './pages/publico/RegistroOferente'

import EmpresaDashboard from './pages/empresa/Dashboard'
import EmpresaPuestos from './pages/empresa/MisPuestos'
import EmpresaNuevoPuesto from './pages/empresa/NuevoPuesto'
import EmpresaCandidatoDetalle from './pages/empresa/CandidatoDetalle'
import EmpresaCandidatos from './pages/empresa/Candidatos'

import OferenteDashboard from './pages/oferente/Dashboard'
import OferenteHabilidades from './pages/oferente/Habilidades'
import OferenteCv from './pages/oferente/MiCV'
import OferentePuestos from './pages/oferente/BuscarPuestos'

import AdminDashboard from './pages/admin/Dashboard'
import AdminEmpresas from './pages/admin/Empresas'
import AdminOferentes from './pages/admin/Oferentes'
import AdminCaracteristicas from './pages/admin/Caracteristicas'
import AdminReportes from './pages/admin/Reportes'
import PuestoDetalle from './pages/publico/PuestoDetalle'

function ProtectedRoute({ children, role }) {
  const { isLogged, role: userRole } = useAuth()
  if (!isLogged) return <Navigate to="/login" replace />
  if (role && userRole !== role) return <Navigate to="/login" replace />
  return children
}

function AppRoutes() {
  return (
      <>
        <Navbar />
        <Routes>
          <Route path="/" element={<Inicio />} />
          <Route path="/login" element={<Login />} />
          <Route path="/buscar" element={<Buscar />} />
          <Route path="/registro/empresa" element={<RegistroEmpresa />} />
          <Route path="/registro/oferente" element={<RegistroOferente />} />

          <Route path="/empresa/dashboard" element={<ProtectedRoute role="ROLE_EMPRESA"><EmpresaDashboard /></ProtectedRoute>} />
          <Route path="/empresa/puestos" element={<ProtectedRoute role="ROLE_EMPRESA"><EmpresaPuestos /></ProtectedRoute>} />
          <Route path="/empresa/puestos/nuevo" element={<ProtectedRoute role="ROLE_EMPRESA"><EmpresaNuevoPuesto /></ProtectedRoute>} />
          <Route path="/empresa/puestos/:id/candidatos" element={<ProtectedRoute role="ROLE_EMPRESA"><EmpresaCandidatos /></ProtectedRoute>} />
          <Route path="/empresa/candidatos/:id" element={<ProtectedRoute role="ROLE_EMPRESA"><EmpresaCandidatoDetalle /></ProtectedRoute>} />

          <Route path="/oferente/dashboard" element={<ProtectedRoute role="ROLE_OFERENTE"><OferenteDashboard /></ProtectedRoute>} />
          <Route path="/oferente/habilidades" element={<ProtectedRoute role="ROLE_OFERENTE"><OferenteHabilidades /></ProtectedRoute>} />
          <Route path="/oferente/cv" element={<ProtectedRoute role="ROLE_OFERENTE"><OferenteCv /></ProtectedRoute>} />
          <Route path="/oferente/puestos" element={<ProtectedRoute role="ROLE_OFERENTE"><OferentePuestos /></ProtectedRoute>} />

          <Route path="/admin/dashboard" element={<ProtectedRoute role="ROLE_ADMIN"><AdminDashboard /></ProtectedRoute>} />
          <Route path="/admin/empresas" element={<ProtectedRoute role="ROLE_ADMIN"><AdminEmpresas /></ProtectedRoute>} />
          <Route path="/admin/oferentes" element={<ProtectedRoute role="ROLE_ADMIN"><AdminOferentes /></ProtectedRoute>} />
          <Route path="/admin/caracteristicas" element={<ProtectedRoute role="ROLE_ADMIN"><AdminCaracteristicas /></ProtectedRoute>} />
          <Route path="/admin/reportes" element={<ProtectedRoute role="ROLE_ADMIN"><AdminReportes /></ProtectedRoute>} />
          <Route path="/puesto/:id" element={<PuestoDetalle />} />

          <Route path="*" element={<div className="container"><h2>Página no encontrada</h2><a className="btn btn-outline" href="/">← Inicio</a></div>} />
        </Routes>
        <Footer />  { }
      </>
  )
}
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}
