export function parseNotebook(jsonStr) {
  try {
    const nb = JSON.parse(jsonStr)
    if (!nb.cells || !Array.isArray(nb.cells)) return []
    return nb.cells.map((cell, index) => {
      const source = (cell.source || []).join('').replace(/\r\n/g, '\n')
      let outputs = []
      if (cell.outputs && cell.outputs.length > 0) {
        outputs = cell.outputs.flatMap(output => {
          if (output.text) return [{ type: 'text', data: output.text.join('') }]
          if (output.data && output.data['image/png']) return [{ type: 'image', data: output.data['image/png'] }]
          if (output.data && output.data['text/plain']) return [{ type: 'text', data: output.data['text/plain'].join('') }]
          return []
        })
      }
      return {
        index: index + 1,
        cellType: cell.cell_type,
        source: source,
        outputs: outputs
      }
    })
  } catch (e) {
    console.error('Notebook 解析失败:', e)
    return []
  }
}
