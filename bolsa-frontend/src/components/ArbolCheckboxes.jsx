export default function ArbolCheckboxes({ raices, seleccionadas = [], mostrarNivel = false, onChange }) {
    const handleCheck = (id, checked) => {
        const newSel = checked
            ? [...seleccionadas, id]
            : seleccionadas.filter(s => s !== id)
        onChange && onChange(newSel)
    }

    // Solo renderiza padre e hijos directos (depth máx 1)
    const renderNodo = (nodo, depth = 0) => (
        <li key={nodo.id} style={{ marginLeft: depth * 16, margin: `6px 0 6px ${depth * 16}px` }}>
            {depth === 0 ? (
                // Padre: solo etiqueta, sin checkbox
                <span style={{ fontWeight: 600, fontSize: 13, color: 'var(--text-primary)' }}>
        {nodo.nombre}
      </span>
            ) : (
                // Hijos con checkbox
                <label>
                    <input
                        type="checkbox"
                        checked={seleccionadas.includes(nodo.id)}
                        onChange={e => handleCheck(nodo.id, e.target.checked)}
                        data-id={nodo.id}
                    />
                    <span style={{ marginLeft: 7 }}>{nodo.nombre}</span>
                </label>
            )}
            {depth === 0 && nodo.hijos && nodo.hijos.length > 0 && (
                <ul style={{ listStyle: 'none', padding: 0 }}>
                    {nodo.hijos.map(h => renderNodo(h, 1))}
                </ul>
            )}
        </li>
    )

    return (
        <div className="arbol">
            <ul style={{ listStyle: 'none', padding: 0 }}>
                {raices.map(r => renderNodo(r, 0))}
            </ul>
        </div>
    )
}
