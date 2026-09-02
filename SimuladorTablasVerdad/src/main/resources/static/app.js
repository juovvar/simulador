document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('expressionInput');
    const tableContainer = document.getElementById('tableContainer');
    const btnClear = document.getElementById('btnClear');
    const btnGenerate = document.getElementById('btnGenerate');
    const btnBackspace = document.getElementById('btnBackspace');
    const themeToggleBtn = document.getElementById('themeToggleBtn');
    const themeIcon = document.getElementById('themeIcon');

    // Asigna eventos a los botones de simbolos del teclado
    document.querySelectorAll('.btn-symbol').forEach(button => {
        button.addEventListener('click', () => {
            const symbol = button.getAttribute('data-symbol');
            if (symbol) {
                input.value += symbol;
            }
        });
    });

    // Accion de limpiar
    btnClear.addEventListener('click', () => {
        input.value = '';
        tableContainer.innerHTML = '';
    });

    // Accion de retroceder
    btnBackspace.addEventListener('click', () => {
        input.value = input.value.slice(0, -1);
    });

    // Accion de generar la tabla
    btnGenerate.addEventListener('click', generateTable);

    function generateTable() {
        const expr = input.value.trim();
        if (!expr) {
            tableContainer.innerHTML =
                '<p class="Txwarning">Por favor, ingrese una expresión proposicional.</p>';
            return;
        }

        fetch('/api/evaluar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ expresion: expr })
        })
            .then(async response => {
                const data = await response.json();
                if (!response.ok) {
                    throw new Error(data.error || 'Error al procesar la expresión');
                }
                return data;
            })
            .then(data => renderTable(data))
            .catch(err => {
                tableContainer.innerHTML =
                    `<p class="Txwarning">${err.message}</p>`;
            });
    }

    function renderTable(data) {
        let html = '<table>';

        // Encabezado
        html += '<thead><tr>';
        data.headers.forEach(header => {
            html += `<th>${header}</th>`;
        });
        html += '</tr></thead>';

        // Filas
        html += '<tbody>';
        data.rows.forEach(row => {
            html += '<tr>';
            row.forEach((val, index) => {
                if (index === row.length - 1) {
                    const resultClass = (val === 'V') ? 'alt_green' : 'alt_red';
                    html += `<td class="${resultClass}"><strong>${val}</strong></td>`;
                } else {
                    html += `<td>${val}</td>`;
                }
            });
            html += '</tr>';
        });
        html += '</tbody></table>';

        tableContainer.innerHTML = html;
    }

    // Logica del modo claro/oscuro
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme === 'dark') {
        document.body.classList.add('dark-mode');
        themeIcon.textContent = '🔌';
    } else {
        themeIcon.textContent = '💡';
    }

    themeToggleBtn.addEventListener('click', () => {
        document.body.classList.toggle('dark-mode');
        const isDarkMode = document.body.classList.contains('dark-mode');
        themeIcon.textContent = isDarkMode ? '🔌' : '💡';
        localStorage.setItem('theme', isDarkMode ? 'dark' : 'light');
    });
});