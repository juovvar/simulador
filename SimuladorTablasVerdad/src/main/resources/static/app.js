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
        tableContainer.replaceChildren();
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
            showWarning('Por favor, ingrese una expresión proposicional.');
            return;
        }

        fetch('/api/evaluar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ expresion: expr })
        })
            .then(async response => {
                // Tolera respuestas no JSON (p. ej. de un proxy intermedio)
                const data = await response.json().catch(() => ({}));
                if (!response.ok) {
                    throw new Error(data.error || 'Error al procesar la expresión');
                }
                return data;
            })
            .then(data => renderTable(data))
            .catch(err => showWarning(err.message));
    }

    // Los mensajes se insertan como texto, nunca como HTML
    function showWarning(message) {
        const p = document.createElement('p');
        p.className = 'Txwarning';
        p.textContent = message;
        tableContainer.replaceChildren(p);
    }

    function renderTable(data) {
        const table = document.createElement('table');

        // Encabezado
        const thead = table.createTHead();
        const headRow = thead.insertRow();
        data.headers.forEach(header => {
            const th = document.createElement('th');
            th.textContent = header;
            headRow.appendChild(th);
        });

        // Filas
        const tbody = table.createTBody();
        data.rows.forEach(row => {
            const tr = tbody.insertRow();
            row.forEach((val, index) => {
                const td = tr.insertCell();
                if (index === row.length - 1) {
                    td.className = (val === 'V') ? 'alt_green' : 'alt_red';
                    const strong = document.createElement('strong');
                    strong.textContent = val;
                    td.appendChild(strong);
                } else {
                    td.textContent = val;
                }
            });
        });

        tableContainer.replaceChildren(table);
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