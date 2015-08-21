function oninitfillforms(fillforms) {
    fillforms.updateAuditPanel({
        render : function(datas) {
            debugger;
            filterAudit(datas);
        }
    });
}
function filterAudit(datas) {
    if (!datas) {
        return;
    }
    var len = datas.length;
    var parent = {};
    for (var i = len - 1; i >= 0; i--) {
        var data = datas[i];
        var isParent = data && data.isParent;
        if (isParent) {
            continue;
        }
        var level = data && data.ppts && data.ppts._level;
        if (level !== "error") {
            datas.splice(i, 1);
        } else {
            var pid = data.pId;
            parent[pid] = pid;
        }
    }
    //如果节点下的所有节点都被删除了，则该节点也删除
    var nlen = datas.length;
    if (len === nlen) {
        return;
    }
    for (var i = nlen - 1; i >= 0; i--) {
        var data = datas[i];
        var isParent = data && data.isParent;
        if (!isParent) {
            continue;
        }
        var pid = data.pId;
        if (pid === 0) {
            continue;
        }
        var id = data.id;
        if (parent[id] == null) {
            datas.splice(i, 1);
        }
    }
    if (datas.length == 1) {
        datas.splice(0, 1);
    }
}
