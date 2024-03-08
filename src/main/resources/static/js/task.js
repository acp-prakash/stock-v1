// Define your constants
const TABLE_CONTAINER_ID = 'TASK';
const DOWNLOAD_BUTTON_ID = 'download_TASK';
const CLEAR_BUTTON_ID = 'clear_TASK';
const DATA_URL = 'task/getTasks';
// Initialize Tabulator
const table = createTASKTable();

function createTASKTable() {
    return new Tabulator(`#${TABLE_CONTAINER_ID}`, {
        height: '60.5%',
        maxHeight: '60.5%',
        placeholder: "<div style='display:inline-block; border:4px solid #333; border-radius:10px; background:#fff; font-weight:bold; font-size:16px; color:#000; padding:10px 20px;margin-left: -7500px;'>Loading Data</div",
        data: [],
        layout: 'fitDataTable',
        pagination: 'local',
        paginationSize: 15,
        paginationCounter: 'rows',
        movableColumns: true,
        //resizableRows: true,
        initialSort: [
            { column: 'date', dir: 'desc' }
        ],
        columns: [
            { title: 'ORDER', field: 'seq', headerFilter: true, width: 80 },
            { title: 'NAME', field: 'id', headerFilter: true, width: 250, frozen: true },
            { title: 'SKIP', field: 'skip', headerFilter: true, width: 65, frozen: true },
            { title: 'RUN DATE', field: 'runDate', headerFilter: true, width: 95 },
            { title: 'STATUS', field:'status', headerFilter:true, width:90},
			{ title: 'TOKEN', field:'token', headerFilter:true,width:150},						
			{ title: 'CK-1', field:'cookie1', sorter:'string',headerFilter:true, width:322},
			{ title: 'CK-2', field:'cookie2', headerFilter:true,width:322},
        ],
    });
}

function getFmtTime(dt) {
    if (dt == null) {
        return dt;
    }
    const newDt = new Date(dt);
    let val = new Date(newDt.getTime() + (-300 * 60 * 1000)).toISOString().slice(0, 19);
    val = val.replace('T', ' ');
    let time = val[11] + val[12];
    if (time > 12) {
        time = time - 12;
        if (time < 10) {
            time = '0' + time;
        }
        val[11] = time[0];
        val[12] = time[1];
    }
    return val;
}

document.getElementById(DOWNLOAD_BUTTON_ID).addEventListener('click', () => {
    table.download('xlsx', 'TASK.xlsx', { sheetName: 'TASK' });
});

document.getElementById(CLEAR_BUTTON_ID).addEventListener('click', () => {
    table.clearHeaderFilter();
    table.clearFilter();
});

fetchTasks();

function fetchTasks() {
    $.ajax({
        type: 'GET',
        url: DATA_URL,
        cache: false,
        contentType: 'application/json;',
        success: function (response) {
            table.replaceData(response);
            table.redraw(true);
        },
        error: function (error) {
            console.log(error);
        }
    });
}