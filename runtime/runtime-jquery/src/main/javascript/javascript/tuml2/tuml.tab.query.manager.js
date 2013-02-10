(function ($) {
    // register namespace
    $.extend(true, window, {
        Tuml:{
            TumlTabQueryManager:TumlTabQueryManager
        }
    });

    function TumlTabQueryManager(instanceQueryUri, classQueryUri, queryId) {

        var self = this;
        var tumlTabGridManager;
        if (queryId !== undefined) {
            this.queryId = queryId;
        }

        function init() {
            tumlTabGridManager = new Tuml.TumlQueryGridManager();
            tumlTabGridManager.onContextMenuClickLink.subscribe(function (e, args) {
                self.onContextMenuClickLink.notify(args, e, self);
            });
            tumlTabGridManager.onSelfCellClick.subscribe(function (e, args) {
                self.onSelfCellClick.notify(args, e, self);
            });
        }

        function createQuery(queryTabDivName, oclExecuteUri, query, post) {
            var self = this;
            var queryTab = $('#' + queryTabDivName);

//            //Create the layout's center and north pane
//            var northDiv = $('<div />', {class: 'ui-layout-north'});
//            var centerDiv = $('<div />', {class: 'ui-layout-center'});
//            northDiv.appendTo(queryTab);
//            centerDiv.appendTo(queryTab);

            $('<div />', {id:'serverErrorMsg_' + queryTabDivName}).appendTo(queryTab);

            //Outer div for entering ocl
            var oclOuter = $('<div />', {id:queryTabDivName + '_' + 'OclOuter', class:'oclouter'});
            oclOuter.appendTo(queryTab);

            //Inner div for entering ocl and buttons
            var oclInner = $('<div />', {id:queryTabDivName + '_' + 'OclInner', class:'oclinner'}).appendTo(oclOuter);
            var oclTextAreaDiv = $('<div />', {class:'ocltextarea'}).appendTo(oclInner);
            $('<textarea />', {id:queryTabDivName + '_' + 'QueryString'}).text(query.queryString).appendTo(oclTextAreaDiv);
            var oclInnerButton = $('<div />', {id:queryTabDivName + '+' + 'OclInnerButton', class:'oclinnerbutton'}).appendTo(oclInner);

            var oclExecuteButtonDiv = $('<div />', {class:"oclexecutebutton"}).appendTo(oclInnerButton);
            $('<button />', {id:queryTabDivName + '_' + 'ExecuteButton'}).click(function () {
                $.ajax({
                    url:oclExecuteUri + '?ocl=' + $("#" + queryTabDivName + '_' + 'QueryString').val(),
                    type:"GET",
                    dataType:"json",
                    contentType:"json",
                    success:function (data, textStatus, jqXHR) {
                        tumlTabGridManager.refresh(data[0], queryTabDivName + '_' + 'OclResult');
                        $('#serverErrorMsg_' + queryTabDivName).removeClass('server-error-msg');
                        $('#serverErrorMsg_' + queryTabDivName).empty();
                        $('#tab-container').tabs('resize');
                    },
                    error:function (jqXHR, textStatus, errorThrown) {
                        $('#serverErrorMsg_' + queryTabDivName).addClass('server-error-msg').html(jqXHR.responseText);
                    }
                });
            }).text('execute').appendTo(oclExecuteButtonDiv);

            var inputEditButtonDiv;
            var oclQueryNameInputDiv;
            var oclEditButtonDiv;
            if (isTumlLib) {
                inputEditButtonDiv = $('<div />', {class:'oclinputeditbutton'}).appendTo(oclInnerButton);
                oclQueryNameInputDiv = $('<div />', {class:'oclqueryname'}).appendTo(inputEditButtonDiv);
                $('<input >', {id:queryTabDivName + '_' + 'QueryName', type:'text'}).val(query.name).appendTo(oclQueryNameInputDiv);
                oclEditButtonDiv = $('<div />', {class:"ocleditbutton"}).appendTo(inputEditButtonDiv);
            }

            if (isTumlLib && instanceQueryUri !== '') {
                $('<button />', {id:queryTabDivName + '_' + 'SaveButton'}).text('save to instance').click(function () {
                    var query = queryToJson(queryTabDivName, self.queryId);
                    $.ajax({
                        url:instanceQueryUri,
                        type:post ? "POST" : "PUT",
                        dataType:"json",
                        contentType:"json",
                        data:JSON.stringify(query),
                        success:function (data, textStatus, jqXHR) {
                            var queryFromDb;
                            var queries = data[0].data;
                            for (var i = 0; i < queries.length; i++) {
                                queryFromDb = queries[i];
                                if (queryFromDb.name == query.name) {
                                    break;
                                }
                            }
                            if (post) {
                                self.onPostInstanceQuerySuccess.notify(
                                    {queryType: 'instanceQuery',query:queryFromDb, gridData:tumlTabGridManager.getResult()}, null, self);
                            } else {
                                self.onPutInstanceQuerySuccess.notify(
                                    {queryType: 'instanceQuery',query:queryFromDb, gridData:tumlTabGridManager.getResult()}, null, self);
                            }
                        },
                        error:function (jqXHR, textStatus, errorThrown) {
                            $('#serverErrorMsg_' + queryTabDivName).addClass('server-error-msg').html(jqXHR.responseText);

                        }
                    });
                }).appendTo(oclEditButtonDiv);
            }

            if (isTumlLib && classQueryUri !== '') {
                $('<button />', {id:queryTabDivName + '_' + 'SaveButton'}).text('save to class').click(function () {
                    var query = queryToJson(queryTabDivName, self.queryId);
                    $.ajax({
                        url:classQueryUri,
                        type:post ? "POST" : "PUT",
                        dataType:"json",
                        contentType:"json",
                        data:JSON.stringify(query),
                        success:function (data, textStatus, jqXHR) {
                            var queryFromDb;
                            var queries = data[0].data;
                            for (var i = 0; i < queries.length; i++) {
                                queryFromDb = queries[i];
                                if (queryFromDb.name == query.name) {
                                    break;
                                }
                            }
                            if (post) {
                                self.onPostClassQuerySuccess.notify(
                                    {queryType: 'classQuery',query:queryFromDb, gridData:tumlTabGridManager.getResult()}, null, self);
                            } else {
                                self.onPutClassQuerySuccess.notify(
                                    {queryType: 'classQuery',query:queryFromDb, gridData:tumlTabGridManager.getResult()}, null, self);
                            }
                        },
                        error:function (jqXHR, textStatus, errorThrown) {
                            $('#serverErrorMsg_' + queryTabDivName).addClass('server-error-msg').html(jqXHR.responseText);

                        }
                    });
                }).appendTo(oclEditButtonDiv);
            }
            if (isTumlLib && !post) {
                $('<button />', {id:queryTabDivName + '_' + 'CancelButton'}).text('cancel').appendTo(oclEditButtonDiv);
                $('<button />', {id:queryTabDivName + '_' + 'DeleteButton'}).text('delete').click(function () {

                        var query = queryToJson(queryTabDivName, id);
                        $.ajax({
                            url:tumlUri,
                            type:"DELETE",
                            dataType:"json",
                            contentType:"json",
                            data:JSON.stringify(query),
                            success:function (data, textStatus, jqXHR) {
                                self.onDeleteQuerySuccess.notify(
                                    {tumlUri:tumlUri,
                                        queryName:query.name,
                                        oclExecuteUri:oclExecuteUri,
                                        queryEnum:query.queryEnum,
                                        queryString:query.queryString,
                                        data:data}, null, self);
                            },
                            error:function (jqXHR, textStatus, errorThrown) {
                                $('#serverErrorMsg_' + queryTabDivName).addClass('server-error-msg').html(jqXHR.responseText);

                            }
                        });


                    }
                ).appendTo(oclEditButtonDiv);
            }

            //Outer div for results
            var oclResult = $('<div />', {id:queryTabDivName + '_' + 'OclResult', class:'oclresult'});
            oclResult.appendTo(centerDiv);
            if (query.data !== undefined && query.data !== null) {
                tumlTabGridManager.refresh(query.data, queryTabDivName + '_' + 'OclResult');
            }

//            queryTab.layout({ resizable: true });

        }

        function queryToJson(queryTabDivName, id) {
            var query = {};
            query.name = $("#" + queryTabDivName + '_' + 'QueryName').val();
            query.queryString = $("#" + queryTabDivName + '_' + 'QueryString').val();
            query.queryEnum = "OCL";
            if (id !== undefined) {
                query.id = id;
            }
            return query;
        }

        //Public api
        $.extend(this, {
            "TumlTabQueryManagerVersion":"1.0.0",
            //These events are propagated from the grid
            "onPutInstanceQuerySuccess":new Tuml.Event(),
            "onPostInstanceQuerySuccess":new Tuml.Event(),
            "onPutClassQuerySuccess":new Tuml.Event(),
            "onPostClassQuerySuccess":new Tuml.Event(),
            "onPutQueryFailure":new Tuml.Event(),
            "onPostQueryFailure":new Tuml.Event(),
            "onDeleteQuerySuccess":new Tuml.Event(),
            "onDeleteSuccess":new Tuml.Event(),
            "onDeleteFailure":new Tuml.Event(),
            "onCancel":new Tuml.Event(),
            "onSelfCellClick":new Tuml.Event(),
            "onContextMenuClickLink":new Tuml.Event(),
            "createQuery":createQuery
        });

        init();
    }

})(jQuery);
