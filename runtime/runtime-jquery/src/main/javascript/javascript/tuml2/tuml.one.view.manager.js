(function ($) {
    // register namespace
    $.extend(true, window, {
        Tuml: {
            TumlOneViewManager: TumlOneViewManager
        }
    });

    function TumlOneViewManager() {

        var self = this;
        var exist = false;
        var tumlTabViewManagers = [];

        function init() {
        }

        function clear() {
            tumlTabViewManagers = [];
        }

        function refresh(tumlUri, result) {
            var isForCreation = true;
            for (var i = 0; i < result.length; i++) {
                if (result[i].data.length > 0) {
                    isForCreation = false;
                    break;
                }
            }
            for (var i = 0; i < result.length; i++) {
                response = result[i];
                metaForData = response.meta.to;
                if (isForCreation || response.data.length > 0) {
                    var tabContainer = $('#tab-container');
                    var tabDiv = $('<div />', {id: metaForData.name, title: metaForData.name, class: 'tumltab'}).appendTo(tabContainer);
                    var tumlTabViewManager = new Tuml.TumlTabViewManager({many: false, one: true, query: false}, tumlUri, response.meta.qualifiedName, metaForData.name);
                    tumlTabViewManager.onPutOneSuccess.subscribe(function(e, args) {
                        self.onPutOneSuccess.notify(args, e, self);
                    });
                    tumlTabViewManager.onPutOneFailure.subscribe(function(e, args) {
                    });
                    tumlTabViewManager.onPostOneSuccess.subscribe(function(e, args) {
                        self.onPostOneSuccess.notify(args, e, self);
                    });
                    tumlTabViewManager.onPostOneFailure.subscribe(function(e, args) {
                    });
                    //Only create the tab if it does not exist. This function is called initially and after an update (PUT)
                    var tabDiv = $('#' + metaForData.name);
                    if (tabDiv.length === undefined || tabDiv == null) {
                        tumlTabViewManager.createTab(result);
                    }
                    tumlTabViewManager.createOne(response.data[0], metaForData);
                    tumlTabViewManagers.push(tumlTabViewManager);
                }
            }
        }

        function closeQuery(title, index) {
            tumlTabViewManagers.splice(index, 1);
        }

        function openQuery(tumlUri, oclExecuteUri, qualifiedName, tabDivName, queryEnum, queryString) {
            //Check is there is already a tab open for this query
            var tumlTabViewManagerQuery; 
            var tabIndex = 0;
            for (j = 0; j < tumlTabViewManagers.length; j++) {
                if (tumlTabViewManagers[j].oneManyOrQuery.query && tumlTabViewManagers[j].tabDivName == tabDivName) {
                    tumlTabViewManagerQuery = tumlTabViewManagers[j];
                    tabIndex = j;
                    break;
                }
            }
            if (tumlTabViewManagerQuery === undefined) {
                $('#tab-container').tabs('add', {title: tabDivName, content: '<div id="'+tabDivName+'" />', closable: true});
                var tumlTabViewManager = new Tuml.TumlTabViewManager({many: false, one: false, query: true}, tumlUri, qualifiedName, tabDivName);
                tumlTabViewManagers.push(tumlTabViewManager);
                tumlTabViewManager.createQuery(oclExecuteUri, queryEnum, queryString);
            } else {
                //Just make the tab active
                $('#tab-container').tabs('select', tabIndex);
            }

        }
        function clear() {
        }

        //Public api
        $.extend(this, {
            "TumlOneViewManagerVersion": "1.0.0",
            "onPutOneSuccess": new Tuml.Event(),
            "onPutOneFailure": new Tuml.Event(),
            "onPostOneSuccess": new Tuml.Event(),
            "onPostOneFailure": new Tuml.Event(),
            "refresh": refresh,
            "openQuery": openQuery,
            "closeQuery": closeQuery,
            "clear": clear
        });

        init();
    }

})(jQuery);
