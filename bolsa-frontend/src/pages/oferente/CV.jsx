import { useEffect, useState } from 'react'
import { oferenteAPI } from '../../api/api'
import { Toast } from '../../components/UI'

export default function OferenteCv() {
  const [perfil, setPerfil] = useState(null)
  const [file, setFile] = useState(null)
  const [toast, setToast] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => { oferenteAPI.perfil().then(setPerfil) }, [])
  if (!perfil) return <div className="loading">Cargando…</div>

  const subir = async () => {
    setError('')
    if (!file) { setError('Seleccione un archivo'); return }
    const res = await oferenteAPI.subirCv(file)
    if (res.error) { setError(res.error); return }
    setToast({ msg: 'CV subido correctamente', ok: true })
    oferenteAPI.perfil().then(setPerfil)
  }

  return (
    <div className="container-narrow">
      {toast && <Toast msg={toast.msg} ok={toast.ok} onClose={() => setToast(null)} />}
      <h2>Mi Currículum (CV)</h2>
      <div className="card">
        {error && <div className="alert-error">{error}</div>}
        {perfil.curriculumPdf && (
          <div className="cv-actual">
            📄 CV actual: <strong>{perfil.curriculumPdf}</strong><br /><br />
            <a className="btn btn-outline" href={`/uploads/${perfil.curriculumPdf}`} target="_blank" rel="noreferrer">Ver CV actual</a>
          </div>
        )}
        <label>Subir nuevo CV (solo PDF, máx. 5MB)</label>
        <input type="file" accept=".pdf" onChange={e => setFile(e.target.files[0])} />
        <button className="btn btn-primary" style={{ marginTop: 12 }} onClick={subir}>Subir CV</button>
      </div>
    </div>
  )
}
