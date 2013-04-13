(function ($) {
    // register namespace
    $.extend(true, window, {
        Tuml: {
            TumlTabContainerManager: TumlTabContainerManager
        }
    });

    function TumlTabContainerManager(tabContainer) {

        var self = this;
        this.tumlTabViewManagers = [];
        this.tabContainer = null;
        this.parentTabContainer = tabContainer;
        this.parentTabContainerManager = null;

        this.getTabContainer = function () {
            return this.tabContainer;
        }

        this.setTabContainer = function (tabContainer) {
            this.tabContainer = tabContainer;
        }

        this.getParentTabContainerManager = function () {
            return this.parentTabContainerManager;
        }

        this.setParentTabContainerManager = function (parentTabContainerManager) {
            this.parentTabContainerManager = parentTabContainerManager;
        }

    }

    TumlTabContainerManager.prototype.refreshContext = function(tumlUri) {
        this.getParentTabContainerManager().refreshContext(tumlUri);
    }


    TumlTabContainerManager.prototype.clearAllTabs = function () {
        for (var i = 0; i < this.tumlTabViewManagers.length; i++) {
            this.tumlTabViewManagers[i].clear();
        }
    }

    TumlTabContainerManager.prototype.addToTumlTabViewManagers = function (tumlChildTabViewManager) {
        this.tumlTabViewManagers.push(tumlChildTabViewManager);
    }

    TumlTabContainerManager.prototype.addNewRow = function () {
        alert("TumlTabContainerManager.prototype.addNewRow must be overriden");
    }

    TumlTabContainerManager.prototype.addTab = function (tabEnum, result, tumlUri, propertyNavigatingTo, options, tabContainer) {
        var metaForData = result.meta.to;

        var tumlTabViewManager;
        if (options.isOne) {
            tumlTabViewManager = new Tuml.TumlTabOneViewManager(tabEnum, tabContainer,
                {propertyNavigatingTo: propertyNavigatingTo,
                    many: !options.isOne,
                    one: options.isOne,
                    query: false,
                    forLookup: options.forLookup,
                    forManyComponent: options.forManyComponent,
                    forOneComponent: options.forOneComponent
                }, tumlUri, result
            );
            tumlTabViewManager.setParentTabContainerManager(this);

            tumlTabViewManager.onOneComponentSaveButtonSuccess.subscribe(function (e, args) {
                alert('commented out');
//                    tumlTabViewManager.getParentTumlTabViewManager().setValue(args.value);
//                    closeTab(tumlTabViewManager);
//                    tabContainer.tabs("enable", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
//                    tabContainer.tabs("option", "active", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
            });
            tumlTabViewManager.onOneComponentCloseButtonSuccess.subscribe(function (e, args) {
                alert('commented out');
//                    closeTab(tumlTabViewManager);
//                    tabContainer.tabs("enable", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
//                    tabContainer.tabs("option", "active", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
            });
            tumlTabViewManager.onPutOneSuccess.subscribe(function (e, args) {
                self.onPutOneSuccess.notify(args, e, self);
            });
            tumlTabViewManager.onPostOneSuccess.subscribe(function (e, args) {
                Tuml.TumlTabContainerManager.prototype.clearAllTabs.call(this);
                self.onPostOneSuccess.notify(args, e, self);
            });
            tumlTabViewManager.onDeleteOneSuccess.subscribe(function (e, args) {
                Tuml.TumlTabContainerManager.prototype.clearAllTabs.call(this);
                self.onDeleteOneSuccess.notify(args, e, self);
            });
            tumlTabViewManager.onClickOneComponent.subscribe(function (e, args) {
                //Get the meta data
                $.ajax({
                    url: args.property.tumlMetaDataUri,
                    type: "GET",
                    dataType: "json",
                    contentType: "application/json",
                    success: function (metaDataResponse, textStatus, jqXHR) {
                        $('#tab-container').tabs('disableTab', tumlTabViewManager.tabTitleName);
                        if (args.data !== null) {
                            metaDataResponse[0].data = args.data;
                        }
                        var tumlOneComponentTabViewManager = addTab(
                            tuml.tab.Enum.Properties,
                            metaDataResponse[0],
                            args.tumlUri,
                            args.property,
                            {forLookup: false, forManyComponent: false, forOneComponent: true, isOne: true, forCreation: true},
                            tumlTabViewManager.createSubTabContainer()
                        );
                        postTabCreate(tumlOneComponentTabViewManager, tabContainer, metaDataResponse[0], true, metaDataResponse[0].meta.to, false, self.tumlTabViewManagers.length - 1);
                        tumlTabViewManager.setProperty(args.property);
                        tumlOneComponentTabViewManager.setParentTumlTabViewManager(tumlTabViewManager);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert('error getting ' + property.tumlMetaDataUri + '\n textStatus: ' + textStatus + '\n errorThrown: ' + errorThrown)
                    }
                });
            });
            tumlTabViewManager.onClickManyComponent.subscribe(function (e, args) {
                //Get the meta data
                $.ajax({
                    url: args.property.tumlMetaDataUri,
                    type: "GET",
                    dataType: "json",
                    contentType: "application/json",
                    success: function (metaDataResponse, textStatus, jqXHR) {
                        $('#tab-container').tabs('disableTab', tumlTabViewManager.tabTitleName);
                        if (args.data !== null) {
                            metaDataResponse[0].data = args.data;
                        }
                        var tumlOneComponentTabViewManager = addTab(
                            tuml.tab.Enum.Properties,
                            metaDataResponse[0],
                            args.tumlUri,
                            args.property,
                            {forLookup: false, forManyComponent: true, isOne: false, forCreation: true}
                        );
                        postTabCreate(tumlOneComponentTabViewManager, tabContainer, metaDataResponse[0], true, metaDataResponse[0].meta.to, false, self.tumlTabViewManagers.length - 1);
                        //TODO fix below can not use tumlTabViewManager as a variable as it represent just the last varriable
//                            tumlTabViewManager.setProperty(args.property);
                        tumlOneComponentTabViewManager.setParentTumlTabViewManager(tumlTabViewManager);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert('error getting ' + property.tumlMetaDataUri + '\n textStatus: ' + textStatus + '\n errorThrown: ' + errorThrown)
                    }
                });
            });
        } else {
            if (options.forManyComponent) {
                tumlTabViewManager = new Tuml.TumlTabManyComponentViewManager(tabEnum, tabContainer,
                    {propertyNavigatingTo: propertyNavigatingTo,
                        many: !options.isOne,
                        one: options.isOne,
                        query: false,
                        forLookup: options.forLookup,
                        forManyComponent: options.forManyComponent
                    }, tumlUri, result
                );
                tumlTabViewManager.setParentTabContainerManager(this);
            } else {
                tumlTabViewManager = new Tuml.TumlTabManyViewManager(tabEnum, tabContainer,
                    {propertyNavigatingTo: propertyNavigatingTo,
                        many: !options.isOne,
                        one: options.isOne,
                        query: false,
                        forLookup: options.forLookup,
                        forManyComponent: options.forManyComponent
                    }, tumlUri, result
                );
                tumlTabViewManager.setParentTabContainerManager(this);
            }
            tumlTabViewManager.onSelectButtonSuccess.subscribe(function (e, args) {
                tumlTabViewManager.getParentTumlTabViewManager().addItems(args.items);
                //Closing the tab fires closeTab event which removes the tumlTabViewManager from the array
                $('#tab-container').tabs('close', args.tabName + " Select");
                $('#' + args.tabName + "Lookup").remove();
                tabContainer.tabs("enable", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
                tabContainer.tabs("option", "active", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
            });
            tumlTabViewManager.onManyComponentSaveButtonSuccess.subscribe(function (e, args) {
                alert('commented out');
//                    tumlTabViewManager.getParentTumlTabViewManager().setValue(args.value);
//                    closeTab(tumlTabViewManager);
//                    tabContainer.tabs("enable", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
//                    tabContainer.tabs("option", "active", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
            });
            tumlTabViewManager.onManyComponentCloseButtonSuccess.subscribe(function (e, args) {
                alert('commented out');
//                    closeTab(tumlTabViewManager);
//                    tabContainer.tabs("enable", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
//                    tabContainer.tabs("option", "active", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
            });
            tumlTabViewManager.onSelectCancelButtonSuccess.subscribe(function (e, args) {
                //Closing the tab fires closeTab event which removes the tumlTabViewManager from the array
                $('#tab-container').tabs('close', args.tabName + " Select");
                $('#' + args.tabName + "Lookup").remove();
                tabContainer.tabs("enable", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
                tabContainer.tabs("option", "active", self.tumlTabViewManagers.indexOf(tumlTabViewManager.getParentTumlTabViewManager()));
            });
            tumlTabViewManager.onAddButtonSuccess.subscribe(function (e, args) {
                $('#tab-container').tabs('disableTab', metaForData.name);
                var tumlLookupTabViewManager = addTab(
                    tuml.tab.Enum.Properties,
                    args.data,
                    args.tumlUri,
                    args.propertyNavigatingTo,
                    {forLookup: true, forManyComponent: false}
                );
                postTabCreate(tumlLookupTabViewManager, tabContainer, args.data, true, args.data.meta.to, false, self.tumlTabViewManagers.length - 1);
                tumlLookupTabViewManager.setParentTumlTabViewManager(tumlTabViewManager);
            });
            tumlTabViewManager.onClickManyComponentCell.subscribe(function (e, args) {
                //Get the meta data.
                $.ajax({
                    url: args.property.tumlMetaDataUri,
                    type: "GET",
                    dataType: "json",
                    contentType: "application/json",
                    success: function (result, textStatus, jqXHR) {

                        for (var i = 0; i < result.length; i++) {
                            var subTabContainer = tumlTabViewManager.createOrReturnSubTabContainer();
                            result[i].data = args.data;
                            var tumlManyComponentTabViewManager = addTab(
                                tuml.tab.Enum.Properties,
                                result[i],
                                args.tumlUri,
                                args.property,
                                {forLookup: false, forManyComponent: true, forOneComponent: false, isOne: false, forCreation: true},
                                subTabContainer
                            );
                            tumlManyComponentTabViewManager.setParentTumlTabViewManager(tumlTabViewManager);
                            postTabCreate(tumlManyComponentTabViewManager, subTabContainer, result[i], false, result[i].meta.to, false, i);
                            tumlTabViewManager.setCell(args.cell);
                            tumlTabViewManager.addToTumlTabViewManagers(tumlManyComponentTabViewManager);
                        }
//                            tabContainer.tabs("disable", self.tumlTabViewManagers.indexOf(tumlTabViewManager));
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert('error getting ' + property.tumlMetaDataUri + '\n textStatus: ' + textStatus + '\n errorThrown: ' + errorThrown)
                    }
                });

            });

            tumlTabViewManager.onClickOneComponentCell.subscribe(function (e, args) {
                console.log('TumlMainViewManager onClickOneComponentCell fired');
                //Get the meta data
                $.ajax({
                    url: args.property.tumlMetaDataUri,
                    type: "GET",
                    dataType: "json",
                    contentType: "application/json",
                    success: function (result, textStatus, jqXHR) {
                        if (args.data.length !== 0) {
                            result[0].data = args.data;
                        }
                        var tumlOneComponentTabViewManager = addTab(
                            tuml.tab.Enum.Properties,
                            result[0],
                            args.tumlUri,
                            args.property,
                            {forLookup: false, forManyComponent: false, forOneComponent: true, isOne: true, forCreation: true}
                        );
                        tumlTabViewManager.setCell(args.cell);
                        tumlOneComponentTabViewManager.setParentTumlTabViewManager(tumlTabViewManager);
                        tabContainer.tabs("disable", self.tumlTabViewManagers.indexOf(tumlTabViewManager));
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        alert('error getting ' + property.tumlMetaDataUri + '\n textStatus: ' + textStatus + '\n errorThrown: ' + errorThrown)
                    }
                });
            });

            tumlTabViewManager.onSelfCellClick.subscribe(function (e, args) {
                self.onSelfCellClick.notify(args, e, self);
            });
            tumlTabViewManager.onContextMenuClickLink.subscribe(function (e, args) {
                self.onContextMenuClickLink.notify(args, e, self);
            });
            tumlTabViewManager.onContextMenuClickDelete.subscribe(function (e, args) {
                self.onContextMenuClickDelete.notify(args, e, self);
            });
        }
        tumlTabViewManager.onAddRowSuccess.subscribe(function (e, args) {
            updateValidationWarningHeader();
        });
        tumlTabViewManager.onRemoveRowSuccess.subscribe(function (e, args) {
            updateValidationWarningHeader();
        });
        tumlTabViewManager.onPutSuccess.subscribe(function (e, args) {
            self.onPutSuccess.notify(args, e, self);
            if (args.data[0].meta.to.qualifiedName === 'tumllib::org::tuml::query::Query') {
                alert('update the tree!');
            }
        });
        tumlTabViewManager.onPutFailure.subscribe(function (e, args) {
            self.onPutFailure.notify(args, e, self);
        });
        tumlTabViewManager.onPostSuccess.subscribe(function (e, args) {
            self.onPostSuccess.notify(args, e, self);
            alert('update the tree!');
            if (args.data[0].meta.to.qualifiedName === 'tumllib::org::tuml::query::Query') {
                var metaDataNavigatingTo = args.data[0].meta.to;
                var metaDataNavigatingFrom = args.data[0].meta.from;
                var contextVertexId = retrieveVertexId(args.tumlUri);
                leftMenuManager.refresh(metaDataNavigatingFrom, metaDataNavigatingTo, contextVertexId);
            }
        });
        tumlTabViewManager.onPostFailure.subscribe(function (e, args) {
            self.onPostFailure.notify(args, e, self);
        });
        tumlTabViewManager.onDeleteSuccess.subscribe(function (e, args) {
            self.onDeleteSuccess.notify(args, e, self);
        });
        tumlTabViewManager.onDeleteFailure.subscribe(function (e, args) {
            self.onDeleteFailure.notify(args, e, self);
        });
        tumlTabViewManager.onCancel.subscribe(function (e, args) {
            self.onCancel.notify(args, e, self);
        });

        tumlTabViewManager.onCloseTab.subscribe(function (e, panelId) {
//                for (var j = 0; j < self.tumlTabViewManagers.length; j++) {
//                    if (panelId === self.tumlTabViewManagers[j].tabDivName) {
//                        self.tumlTabViewManagers[j].closeTab();
//                    }
//                }
//                tabContainer.tabs("refresh");
        });

        return tumlTabViewManager;

    }


})(jQuery);
