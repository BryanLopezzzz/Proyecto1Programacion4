import { useEffect, useState } from 'react'
import { oferentePerfil, oferenteSubirCv } from '../../api/api'

export default function MiCV() {
  const [perfil, setPerfil] = useState(null)
  const [file, setFile] = useState(null)
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const cargar = () => oferentePerfil().then(setPerfil).catch(() => {})
  useEffect(() => { cargar() }, [])

  const subir = async () => {
    setError(''); setOk('')
    if (!file) return setError('Seleccione un archivo')
    try {
      const res = await oferenteSubirCv(file)
      if (res.error) throw new Error(res.error)
      setOk('CV subido correctamente')
      cargar()
    } catch (e) { setError(e.message) }
  }

  if (!perfil) return <div className="loading">Cargando…</div>

  return (
    <div className="container-narrow" style={{ padding: '40px 10%', maxWidth: 720 }}>
      <h2>Mi Currículum (CV)</h2>
      <div className="card">
        {error && <div className="alert-error">{error}</div>}
        {ok && <div className="alert-success">{ok}</div>}
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
