function constructRepMgrApp(){
	try {

		isc.setAutoDraw(false);
		
		var topPane = isc.HLayout.create({
			backgroundColor: ctrlColor,
			layoutTopMargin: 2,
			membersMargin:4,
			height: 25,
			width: "100%",
			members: [isc.Label.create({
				contents:"<span class='qrmMajorTitle'>Quay Risk Manager Repository Manager<span>",
				height:25,
				width:"100%"
			}),
			isc.Label.create({
				height:25,
				ID:"qrmHeaderUserName",
				width:100
			}),
			isc.Label.create({
				contents:"<span style='cursor:pointer;font-size:10;color:blue'>Sign Out<span>",
				click:function(){
				isc.RPCManager.sendRequest({
					prompt:"Logging Out",
					evalResult: true,
					params: {
					"OPERATION": "logout"
				},
				actionURL: "./QRMServer"
				});				
				},
				height:25,
				width:60
			})],
			layoutBottomMargin: 2,
			autoDraw: false
		});

		ServerPane = defineServerControlPane();
		RepPane = defineRepositoryPane();
		SchedJobPane = defineScheduledJobPane();
		ComplJobPane = defineCompletedJobPane();
		
		TabSet.create({
			 ID:"RepMgrTabSet",
			 paneContainerProperties:{backgroundColor : ctrlColor},
			 backgroundColor : ctrlColor,
			 tabBarPosition : "top",
		     tabs: [
		         {title:"Server Control", pane: ServerPane},
		         {title:"Repositories", pane:RepPane},
		         {title:"Scheduled Jobs", pane: SchedJobPane},
		         {title:"Completed Jobs", pane:ComplJobPane}
		     ]
		 });
	
		PortalPane = isc.VLayout.create({
			backgroundColor: ctrlColor,
			membersMargin: 2,
			layoutMargin: 2,
			layoutTopMargin: 2,
			height: "100%",
			width: "100%",
			members: [topPane,
			          RepMgrTabSet],
			layoutBottomMargin: 2,
			layoutRightMargin: 2,
			layoutLeftMargin: 2,
			autoDraw: true
		});

	} 
	catch (e) {
		alert(e.message);
	}
}
function defineRepositoryPane(){
	isc.VLayout.create({
		membersMargin:5,
		width:"100%",
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"RepositoryPane"
	});
	
	isc.ListGrid.create({
		ID:"RepGrid",
		width:"100%",
		height:"100%",
		alternateRecordStyles : true,
		fields : [{name:"rep", showTitle:true, title:"Repository",type : "select",width:200}, 
		          {	  name : "repmgr",
		        	  width : "200",
		        	  title : "Repository Risk Manager",
		        	  required : true,
		        	  type : "select",
		        	  allowEmptyValue : false,
		        	  optionDataSource: "allUsersDS", 
		        	  displayField:"name",
		        	  valueField : "stakeholderID"
		          }, {
		        	  name : "active",
		        	  width : "70",
		        	  title : "Active",
		        	  required : true,
		        	  type : "boolean"
		          }, {
		        	  name : "autoAddUsers",
		        	  width : "120",
		        	  title : "Auto Add Users",
		        	  required : true,
		        	  type : "boolean"
		          }, {
		        	  name : "sessionlimit",
		        	  title : "Session Limit",
		        	  width : "80",
		        	  required : true,
		        	  align:"center",
		        	  editorType: "spinner", 
		        	  min: -1, max: 100, step: 1, width: 95
		          }, {
		        	  name : "sessions",
		        	  width : "80",
		        	  align:"center",
		        	  title : "# Sessions",
		        	  required : true,
		        	  type:"text"
		          }, {
		        	  name : "userlimit",
		        	  title : "User Limit",
		        	  width : "50",
		        	  align:"center",
		        	  required : true,
		        	  editorType: "spinner", 
		        	  min: -1, max: 100, step: 1, width: 95
		            }, {
		        	  name : "url",
		        	  width : "400",
		        	  title : "Access URL",
		        	  required : true,
		        	  type : "text"
		            }],
				getBaseStyle: function(record, rowNum, colNum){
						if (colNum == 5 ) {
							if (record.sessionlimit == -1){
								return "rankItem1";
							}
							if (record.sessions == record.sessionlimit ){
								return "rankItem4";
							}
							if (record.sessions >= record.sessionlimit-3){
								return "rankItem3";
							}
							return "rankItem1";
						} else {
							return this.Super("getBaseStyle", arguments);
						}
				},
				recordDoubleClick: function(){			
					editRepository();
					var record = this.getSelectedRecord();
					if (record === null) {
							return
					}
					repositoryForm.setData(record);
				}
	});
	
	var repButtons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   getRepositories();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Edit Repository",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
							editRepository();
							var record = RepGrid.getSelectedRecord();
							if (record === null) {
									return
							}
							repositoryForm.setData(record);
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Populate With Test Data",
		        	   width:145,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   seedRepository();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "New Repository",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   newRepository();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Reposiotry",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        			isc.RPCManager.sendRequest( {
		        				evalResult : false,
		        				params : {
				  				"repURL":RepGrid.getSelectedRecord().url, 
		        				"NONCACHE":Math.random()
		        			},
		        			callback : function(rpcResponse, dataJS, rpcRequest) {
		        				data = JSON.parse(dataJS,dateParser);
		        				reps = data[0];
		        				orgUsers = data[1];
		        				repMap.clear();
		        				for (var i = 0; i < reps.length; i++){
		        					repMap.put(reps[i].repID, reps[i]);
		        				}
		        				getRepositoryEditForm();
		        				RepGrid.setData(reps);
		        			},
		        			actionURL : "./deleteRepository"
		        			});
						}
		        	   }
		           )]
	});

	RepositoryPane.addMember(RepGrid);
	RepositoryPane.addMember(repButtons);
	
	return RepositoryPane;
}
function defineServerControlPane(){
	isc.VLayout.create({
		membersMargin:5,
		width:"100%",
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"ServerControlPane"
	});
	
	isc.HLayout.create({
		membersMargin:5,
		width:"100%",
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"mainH"
	});

	ServerControlPane.addMember(mainH);

	
	isc.VLayout.create({
		membersMargin:5,
		width:350,
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"leftV"
	});
	
	mainH.addMember(leftV);

	isc.VLayout.create({
		membersMargin:5,
		width:650,
		height:"100%",
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"leftV2"
	});

	mainH.addMember(leftV2);
	
	isc.HLayout.create({
		membersMargin:5,
		width:800,
		height:50,
		autoDraw:true,
		backgroundColor: ctrlColor,
		ID:"leftV3"
	});

//	mainH.addMember(leftV3);
	mainH.addMember(getSessionPane());

	isc.ListGrid.create({
		ID:"QueueGrid",
		width:"100%",
		height:150,
		alternateRecordStyles : true,
		fields : [
		          {name:"method", showTitle:true, title:"Queue",type : "text",width:"100%"}, 
		          {name:"count", showTitle:true, title:"Length",type : "text",width:100, align:"center"}]
	});	
	isc.ListGrid.create({
		ID:"TransactionGrid",
		width:"100%",
		height:"100%",
		alternateRecordStyles : true,
		sortFieldNum: 1,
		sortDirection: "descending",
		fields : [
		          {name:"method", showTitle:true, title:"Method",type : "text",width:"100%"}, 
		          {name:"count", showTitle:true, title:"Count",type : "text",width:100, align:"center"}]
	});
	
	isc.ListGrid.create({
		ID:"PeopleGrid",
		width:"100%",
		height:"100%",
		dataSource:allUsersDS,
		autoFetch:true,
		alternateRecordStyles : true,
		fields : [
		          {name:"name", showTitle:true, title:"Name",type : "text",width:150}, 
		          {name:"email", showTitle:true, title:"Email",type : "text",width:"100%"},
		          {name:"allowLogon", showTitle:true, title:"Allow Logon",type : "boolean",width:100}]
	});
	
	var pplButtons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:130,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   PeopleGrid.fetchData({nocache:Math.random()});		        	   }
		           }),isc.IButton.create( {
		        	   title : "Delete",
		        	   width:130,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   deleteUser();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Disable Logon",
		        	   width:130,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   disableLogon();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Enable Logon",
		        	   width:130,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   enableLogon();
		        	   }
		           })]
	});


	
	
	var buttons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   refreshServerControl();
		        	   }
		           })]
	});

	leftV.addMember(isc.Label.create({height:25,contents:"<b>Work Queues</b>"}));
	leftV.addMember(QueueGrid);
	leftV.addMember(isc.Label.create({height:25,contents:"<b>Server Functions</b>"}));
	leftV.addMember(TransactionGrid);

	leftV2.addMember(isc.Label.create({height:25,contents:"<b>Stakeholders</b>"}));
	leftV2.addMember(PeopleGrid);
	leftV2.addMember(pplButtons);

	leftV3.addMember(isc.DynamicForm.create({
		numCols:4,
		width:600,
		titleWidth:200,
		padding:10,
	    fields: [
	        {name: "message",type: "text", width:300, title:"Broadcast Message", required: true},
	        {type:"button", startRow:false, endRow:true, name:"Send", align:"right",click:"sendBroadcastMessage(this.form.getItem('message').getValue())"}
	        ]
	}));
	
	ServerControlPane.addMember(leftV3);
	ServerControlPane.addMember(buttons);
	
	return ServerControlPane;
}
function defineScheduledJobPane(){

	var schedReportTable = isc.ListGrid.create( {
		autoDraw : false,
		ID:"schedJobTable",
		selectionType: "single",
		headerHeight: 40,
		modalEditing: true,
		alternateRecordStyles : true,
		height : "100%",
		width:"100%",
		canEdit:false,
		emptyMessage : "No Scheduled Reports",
		updateJobScheduleTable:function(){
			isc.RPCManager.showPrompt = false;
			prompt:"Retrieving Scheduled Jobs",
			isc.RPCManager.sendRequest( {
				evalResult : true,
				params : {
					"OPERATION" : "getUserScheduledJobs"
				},
				callback : function(rpcResponse, jobs, rpcRequest) {

					try {
						var d = new Date();
						var len=jobs.length;
						for(var i=0; i<len; i++) {
							var record = jobs[i];
							var timeBits = record.timeStr.split(":");
							var hour = parseInt(timeBits[0],10);
							var min = parseInt(timeBits[1],10);
							d.setUTCHours(hour);
							d.setUTCMinutes(min);
							d.setUTCSeconds(0);					
							record.timeStr = d.toLocaleTimeString();
						}
						schedJobTable.setData(jobs);
					} catch(e){
						alert(e.message);
					}
				},
				actionURL : "./QRMServer"
			});
		},
		recordDoubleClick: function(){	
			getScheduleReportWindow().show();
			scheduleReportForm.setValues(schedJobTable.getSelectedRecord());
			DescendantForm2.setValues(schedJobTable.getSelectedRecord());
			QRMNavigator2.selectRecord(schedJobTable.getSelectedRecord());
			var index = -1;
			try {
			for (var i = 0; QRMNavigator2.getRecord(i).projectID != schedJobTable.getSelectedRecord().projectID &&  i < 1000; i++ ){
				index++;
			}
			} catch (e){
				// can happen if 
			}
			QRMNavigator2.selectSingleRecord(index+1);
			
		},
		getBaseStyle: function(record, rowNum, colNum){
			if (colNum == 1 && (record.reportID == -50000 || record.reportID == -50001)) {
				return "qrmRiskExportEntry";
			}
			return this.Super("getBaseStyle", arguments);
		},
		fields : [{
			name:"description", 
			title:"Description",
			type: "text",
			width:150,
			align:"left"
		},{
			name : "reportID",
			title : "Report Type",
			type:"select",
			width : 170,
			align : "center"
		},{
			name : "projectID",
			title : "Project",
			type:"select",
			width : 130,
			align : "center"
		},{
			name : "descendants",
			title : "Desc",
			type:"boolean",
			width : 50,
			align : "center"
		},{ 
			name:"Mon",
			type:"boolean",
			width:40
		},{ 
			name:"Tue",
			type:"boolean",
			width:40
		},{ 
			name:"Wed",
			type:"boolean",
			width:40
		},{ 
			name:"Thu",
			type:"boolean",
			width:40
		},{ 
			name:"Fri",
			type:"boolean",
			width:40
		},{ 
			name:"Sat",
			type:"boolean",
			width:40
		},{ 
			name:"Sun",
			type:"boolean",
			width:40
		},{ 
			title:"Time",
			name:"timeStr",
			type:"text",
			width:80,
			align:"center"
		},{ 
			title:"Email",
			name:"email",
			type:"boolean",
			width:50,
			align:"center"
		},{
			name:"database", 
			title:"Organisation",
			type: "text",
			width:150,
			align:"center"
		},{
			name:"databaseUser", 
			title:"Database URL",
			type: "text",
			width:"100%",
			align:"left"
		}],
		headerSpans: [{
			fields: ["Mon","Tue", "Wed", "Thu","Fri", "Sat", "Sun"], 
			title: "Days to Run"
		}]

	});
	
	var buttons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   getScheduledJobs();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Job",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   deleteJob();
		        	   }
		           })]
	});
	
	var pane = isc.VLayout.create({
		backgroundColor: ctrlColor,
		membersMargin: 2,
		layoutMargin: 2,
		layoutTopMargin: 2,
		height: "100%",
		width: "100%",
		members: [schedReportTable, buttons],
		layoutBottomMargin: 2,
		layoutRightMargin: 2,
		layoutLeftMargin: 2,
		autoDraw: true
	});
	
	return pane;
}
function defineCompletedJobPane(){

	isc.ListGrid.create( {
		ID:"completedJobTable",
		autoDraw : false,
		alternateRecordStyles : true,
		height : "100%",
		emptyMessage : "No Report or Analysis Jobs Found",
		fields : [{
			name:"jobID", 
			title:"Job ID",
			type: "text",
			width:80,
			align:"center"
		},{
			name : "collected",
			title : "Viewed",
			type:"boolean",
			width : "70",
			align : "center"
		},{
			name : "jobDescription",
			title : "Description",
			width : 200,
			align : "left"
		}, {
			name : "queuedDate",
			title : "Queued Date",
			width : 200,
			align : "center", type:"time",
			formatCellValue: function (value) {
				if (value) {
					return value.toDateString ()+"  "+value.toLocaleTimeString ();
				}
			}
		}, {
			name : "executedDate",
			title : "Completed",
			width : 200,
			align : "center", type:"date",
			formatCellValue: function (value) {
				if (value) {
					return value.toDateString ()+"  "+value.toLocaleTimeString ();
				} else {
					return "Pending";
				}
			}
		},{
			name : "jobJdbcURL",
			title : "Database URL",
			width : 400,
			align : "left"
		}]});	
	
	var buttons = isc.HLayout.create( {
		height : 30,
		backgroundColor: ctrlColor,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   getCompletedJobResult();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Two Week Old",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   deleteJobResult(14);
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Two Day Old",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   deleteJobResult(2);
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Delete Job",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        			isc.RPCManager.sendRequest( {
		        				evalResult : false,
		        				params : {
		        				"OPERATION" : "deleteCompletedJob",
		        				"jobID": completedJobTable.getSelectedRecord().jobID,
		        				"url":completedJobTable.getSelectedRecord().jobJdbcURL,
		        				"NONCACHE":Math.random()
		        			},
		        			callback : function(rpcResponse, dataJS, rpcRequest) {
		        				try {
		        					completedJobTable.setData(JSON.parse(dataJS, dateParserDebug));
		        				} catch (e){alert (e.message);}
		        			},
		        			actionURL : "./sessionControl"
		        			});
		        	   }
		           })]
	});
	
	var pane = isc.VLayout.create({
		backgroundColor: ctrlColor,
		membersMargin: 2,
		layoutMargin: 2,
		layoutTopMargin: 2,
		height: "100%",
		width: "100%",
		members: [completedJobTable, buttons],
		layoutBottomMargin: 2,
		layoutRightMargin: 2,
		layoutLeftMargin: 2,
		autoDraw: true
	});
	
	return pane;
}

function disableLogon(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"stakeholderID":PeopleGrid.getSelectedRecord().stakeholderID,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		refreshServerControl();
		PeopleGrid.fetchData({nocache:Math.random()});
	},
	actionURL : "./disableLogon"
	});	
}
function enableLogon(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"stakeholderID":PeopleGrid.getSelectedRecord().stakeholderID,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		refreshServerControl();
		PeopleGrid.fetchData({nocache:Math.random()});
	},
	actionURL : "./enableLogon"
	});	
}
function getSessionPane(){
	
	isc.ListGrid.create({
		ID:"SessionGrid",
		width:"100%",
		height:"100%",
		timeFormatter:"toLong24HourTime",
		alternateRecordStyles : true,
		fields : [{
					 name:"name", 
					 title:"User Name",
					 type : "text",
					 width:200
					},{
					  name : "orgName",
		        	  width : "100",
		        	  title : "Organisation"
		          }, {
		        	  name : "dbUser",
		        	  width : "100",
		        	  title : "Database User",
		        	  type : "text"
		          }, {
		        	  name : "sessionID",
		        	  width : "220",
		        	  title : "Session ID",
		        	  type : "text"
		          }, {
		        	  name : "lastAccess",
		        	  width : "180",
		        	  title : "Last Access",
		        	  type : "time"
		          }, {
		        	  name : "numTransactions",
		        	  width : "70",
		        	  title : "Transactions",
		        	  type : "text",
		        	  align:"center"
		          }, {
		        	  name : "sessionEnabled",
		        	  width : "120",
		        	  title : "Session Enabled",
		        	  required : true,
		        	  type : "boolean"
		          }, {
		        	  name : "remoteHost",
		        	  width : "220",
		        	  title : "Remote Host",
		        	  type : "text"
		          }, {
		        	  name : "remoteAddr",
		        	  width : "220",
		        	  title : "Remote Address",
		        	  type : "text"
		          }]
	});
	
	var sessionButtons = isc.HLayout.create( {
		height : 30,
		membersMargin : 5,
		layoutMargin : 5,
		backgroundColor: ctrlColor,
		members : [isc.Label.create({width:"100%"}),
		           isc.IButton.create( {
		        	   title : "Refresh",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   getSessions();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Disable Session",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   disableSession();
		        	   }
		           }), isc.IButton.create( {
		        	   title : "Clear Defunct Sessions",
		        	   width:140,
		        	   autoDraw : false,
		        	   click : function(form, item, value) {
		        		   clearDefunct();
		        	   }
		           })]
	});
	
	var pane = isc.VLayout.create({
		backgroundColor: ctrlColor,
		membersMargin: 2,
		layoutMargin: 2,
		layoutTopMargin: 2,
		height: "100%",
		width: "100%",
		members: [isc.Label.create({height:25,contents:"<b>Active Sessions</b>"}),SessionGrid, sessionButtons],
		layoutBottomMargin: 2,
		layoutRightMargin: 2,
		layoutLeftMargin: 2,
		autoDraw: true
	});
	
	return pane;
}
function getRepositories(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		data = JSON.parse(dataJS,dateParser);
		reps = data[0];
		orgUsers = data[1];
		repMap.clear();
		for (var i = 0; i < reps.length; i++){
			repMap.put(reps[i].repID, reps[i]);
		}
		getRepositoryEditForm();
		RepGrid.setData(reps);
	},
	actionURL : "./getAllRepositories"
	});
}
function getSessions(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "getAllSessions",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		SessionGrid.setData(JSON.parse(dataJS,dateParserTime2));
	},
	actionURL : "./sessionControl"
	});
}
function getScheduledJobs(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "getAllScheduledJobs",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
		schedJobTable.setData(JSON.parse(dataJS));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});
}
function deleteJob(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "deleteJob",
		"jobID":schedJobTable.getSelectedRecord().internalID,
		"repository":schedJobTable.getSelectedRecord().repository,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
		schedJobTable.setData(JSON.parse(dataJS));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});
}
function disableSession(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "disableSession",
		"sessionID" : SessionGrid.getSelectedRecord().sessionID,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		SessionGrid.setData(JSON.parse(dataJS));
	},
	actionURL : "./sessionControl"
	});
}
function clearDefunct(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "clearDefunct",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		SessionGrid.setData(JSON.parse(dataJS));
	},
	actionURL : "./sessionControl"
	});
}
function getRepositoryEditForm(){
	
	isc.DynamicForm.create( {
		ID : "repositoryForm",
		numCols : 2,
		autoDraw : false,
		titleWidth : 250,
		padding:10,
		hiliteRequiredFields : true,
		width : "100%",
		fields : [{
		        	type:"header", defaultValue:"Edit Repository"
		         },{
		        	 name:"rep", 
		        	 showTitle:true, 
		        	 editorType:"text", 
		        	 title:"Repository",
		        	 width:240, 
		        	 required : true
  	        	},{
		        	  name : "repmgr",
		        	  width : 320,
		        	  title : "Repository Risk Manager",
		        	  required : true,
		        	  type : "comboBox",
		        	  allowEmptyValue : false,
		        	  startRow : true,
		        	  optionDataSource: "allUsersDS", 
		        	  pickListWidth : 320,
		        	  displayField:"name",
		        	  valueField : "stakeholderID"
		          },{
		        	  name : "url",
		        	  width : "600",
		        	  title : "Access URL",
		        	  required : true,
		        	  type : "text",
		        	  allowEmptyValue : false,
		        	  startRow : true
		          },{
		        	  name : "active",
		        	  width : "200",
		        	  title : "Active",
		        	  required : true,
		        	  type : "boolean",
		        	  allowEmptyValue : false,
		        	  startRow : true
		          },{
		        	  name : "autoAddUsers",
		        	  width : "200",
		        	  title : "Auto Add Users",
		        	  required : true,
		        	  type : "boolean",
		        	  allowEmptyValue : false,
		        	  startRow : true
		          },{
		        	  name : "sessionlimit",
		        	  width : "200",
		        	  title : "Session Limit",
		        	  required : true,
		        	  editorType: "spinner", 
		        	  min: -1, max: 100, step: 1, width: 70,
		        	  startRow : true
		          },{
		        	  name : "userlimit",
		        	  width : "200",
		        	  title : "User Limit",
		        	  required : true,
		        	  editorType: "spinner", 
		        	  min: -1, max: 100, step: 1, width: 70,
		        	  startRow : true
		          },{
		        	  name : "repLogonMessage",
		        	  width : "600",
		        	  height:"150",
		        	  title : "Logon Message",
		        	  required : true,
		        	  type : "textArea",
		        	  allowEmptyValue : true,
		        	  startRow : true
		          },{
		        	  name: "validateBtn", 
		        	  title: "Save Changes", 
		        	  type: "button", 
		        	  colSpan:2, 
		        	  endRow:true, 
		        	  align:"right",
		        	  click:function(){
		        	  	isc.RPCManager.sendRequest({
		        	  		actionURL: "./updateRep",
		        	  		evalResult: true,
		        	  		params: {
		        	  			"DATA": JSON.stringify(repositoryForm.getValues())
		  					},
		  					callback: function(rpcResponse, data2, rpcRequest){
		  						alert(data2);
		  						modalEditRepWindow.hide();
		  						getRepositories();
			  				}
						});
		          	}
		       
		          }]
	});
	
	return repositoryForm;

}
function editRepository(){

		isc.Window.create({
			ID: "modalEditRepWindow",
			title: "Edit Repository",
			width:900,
			height:500,
			autoCenter: true,
	    	isModal: true,
	    	showModalMask: true,
	    	showMinimizeButton:false,
	    	autoDraw: false,
	    	closeClick : function () { this.Super("closeClick", arguments);},
	    	items: [getRepositoryEditForm()]
		});
	modalEditRepWindow.show();
}
function seedRepository(){

	if (typeof (modalSeedRepWindow) == "undefined"){
		isc.Window.create({
			ID: "modalSeedRepWindow",
			title: "Seed Reoository",
			width:500,
			height:320,
			autoCenter: true,
	    	isModal: true,
	    	showModalMask: true,
	    	showMinimizeButton:false,
	    	autoDraw: false,
	    	closeClick : function () { this.Super("closeClick", arguments);},
	    	items: [getSeedForm()]
		});
	}
	modalSeedRepWindow.show();
}
function getSeedForm(){
	isc.DynamicForm.create( {
		ID : "seedForm",
		numCols : 2,
		autoDraw : false,
		titleWidth : 250,
		padding:10,
		width : "100%",
		fields : [{
        	type:"header", defaultValue:"Seed Repository"
        	}, { 
 			   name: "createProjectStructure", title: "Create Project Structure", type:"boolean", defaultValue:true
        	}, { 
  			   name: "createTestUsers", title: "Create Test Users", type:"boolean", defaultValue:true
	        },{
			   name: "numRisks", title: "Number of Risks", editorType: "spinner", defaultValue: 0, min: 0, max: 1000, step: 10
	        },{
			   name: "maxNumControlsPerRisk", title: "Max Number of Controls", editorType: "spinner", defaultValue: 5,  min: 0, max: 20, step: 1
			},{
			   name: "maxNumberObjectivesPerRisk", title: "Max Number of Objectives", editorType: "spinner", defaultValue: 5, min: 0, max: 20, step: 1
		   },{
			  name: "maxNumberMitigationStepsPerRisk", title: "Max Number of Mitigation", editorType: "spinner", defaultValue: 10, min: 0, max: 20, step: 1
			},{
			   name: "maxNumberCommentsPerRisk", title: "Max Number of Comments", editorType: "spinner", defaultValue: 5, min: 0, max: 20, step: 1
		  },{
			   name: "maxNumberConsequences", title: "Max Number of Consequences", editorType: "spinner", defaultValue: 5, min: 0, max: 20, step: 1
          },{
				_constructor: "RowSpacerItem"
		  },{
        	  name: "validateBtn", 
        	  title: "Seed Repository", 
        	  type: "button", 
        	  colSpan:2, 
        	  endRow:true, 
        	  align:"right",
        	  click:function(){
		  		isc.RPCManager.sendRequest({
		  			actionURL: "./normaliseProject",
		  			showPrompt:true,
		  			prompt:"Seeding Repository with Data...Please Standby",
		  			evalResult: true,
		  			params: {
		  				"repURL":RepGrid.getSelectedRecord().url, 
		  				"repDBUser":"",
		  				"createProjectStructure":seedForm.getItem("createProjectStructure").getValue(),
		  				"createTestUsers":seedForm.getItem("createTestUsers").getValue(),
		  				"maxNumControlsPerRisk":seedForm.getItem("maxNumControlsPerRisk").getValue(),
		  				"maxNumberObjectivesPerRisk":seedForm.getItem("maxNumberObjectivesPerRisk").getValue(),
		  				"maxNumberMitigationStepsPerRisk":seedForm.getItem("maxNumberMitigationStepsPerRisk").getValue(),
		  				"maxNumberCommentsPerRisk":seedForm.getItem("maxNumberCommentsPerRisk").getValue(),
		  				"numRisks":seedForm.getItem("numRisks").getValue(),
		  				"maxNumberConsequences":seedForm.getItem("maxNumberConsequences").getValue()
		  		},
		  		callback: function(rpcResponse, list, rpcRequest){
		  			alert("Population of Data Complete");
		  			modalSeedRepWindow.hide();
		  		}
		  		});
          	}
          }
		]
	});
	
	return seedForm;

}
function newRepository(){
	isc.DynamicForm.create({
		width:550,
		titleWidth:180,
		padding:10,
	    ID: "addRepositoryForm",
	    fields: [
	        {type:"header", defaultValue:"Create New Repository"},
	          {name:"repTitle", showTitle:true, editorType:"text", title:"Repository",width:240, required : true},
	          {name:"reporgcode", showTitle:true, editorType:"text", title:"Organisation Code",width:240, required : true},
            {name:"RowSpacerItem0", _constructor:"RowSpacerItem"},
	        	{
	        	  name : "repmgr",
	        	  width : 320,
	        	  title : "Repository Risk Manager",
	        	  type : "comboBox",
	        	  allowEmptyValue : false,
	        	  optionDataSource: "allUsersDS", 
	        	  pickListWidth : 320,
	        	  displayField:"compoundName",
	        	  valueField : "stakeholderID"
	        	},
	        	 {_constructor:"StaticTextItem", defaultValue:"or", title:""},
	        	{
		        	  name : "repname",
		        	  width : 320,
		        	  title : "New Risk Manager Name",
		        	  type : "text",
		        	  allowEmptyValue : false,
		        	  startRow : true
	        	}, {
		        	  name : "repemail",
		        	  width : 320,
		        	  title : "New Risk Manager Email",
		        	  type : "text",
		        	  allowEmptyValue : false,
		        	  startRow : true
	        	}, {
		        	  name : "reppass",
		        	  width : 320,
		        	  title : "New Risk Manager Password",
		        	  type : "text",
		        	  allowEmptyValue : false,
		        	  startRow : true
		       },
		       {name:"RowSpacerItem0", _constructor:"RowSpacerItem"},

	          {name: "validateBtn", title: "Submit", type: "button", align:"right", colSpan:2, 
	        	click: function(){
	        	  try{
	        		if (addRepositoryForm.validate()){
	        			isc.RPCManager.sendRequest({
	        				prompt:"Adding Repository",
	        				evalResult: true,
	        				params: {
	        				"OPERATION": "addRepository",
	        				"DATA":JSON.stringify(addRepositoryForm.getValues())
	        			},
	        			callback: function(rpcResponse, data2, rpcRequest){
	        				alert(data2);
	        				getRepositories();
	     	        		modalAddRepWindow.hide();
	        			},
	        			actionURL: "./addRepository"
	        			});
	        		} else {
	        			alert("Not Valid for some reason");
	        		}
	            } catch (e){alert(e.message);}
	          }
	        }
	        ]
	});
	
	isc.Window.create({
	    ID: "modalAddRepWindow",
	    title: "New Repository",
	    autoSize:true,
	    autoCenter: true,
	    isModal: true,
	    showModalMask: true,
	    showMinimizeButton:false,
	    autoDraw: false,
	    closeClick : function () { this.Super("closeClick", arguments);},
	    items: [addRepositoryForm]
	});

	modalAddRepWindow.show();

}
function sendBroadcastMessage(value){
	
	isc.RPCManager.sendRequest({
		actionURL: './sessionControl',
		evalResult: true,
		params: {"OPERATION": "sendBroadcastMessage","DATA": value}
	});
	broadcastWindow.hide();
}
function dateParser(key, value) {
	var a;
	if (typeof value === 'string') {
		a = /^(\d{4})-(\d{2})-(\d{2})/.exec(value);
		if (a) {
			return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3]));
		}
	}
	return value;
}
function dateParserTime2(key, value) {
	var a;
	if (typeof value === 'string') {
		a = /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2}).(\d{3})/.exec(value);
		if (a) {
			return new Date(a[1], +a[2] - 1, +a[3], a[4],a[5],a[6],a[7]);
		}
	}
	return value;
}
function dateParserDebug(key, value) {
	var a;
	if (typeof value === 'string') {
		a = /^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})/.exec(value);
		if (a) {
			return  new Date(+a[1], +a[2] - 1, +a[3], +a[4], +a[5], +a[6], 0);
		}

		a = /^(\d{4})-(\d{2})-(\d{2})/.exec(value);
		if (a) {
			return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3]));
		}
	}
	return value;
}
function deleteJobResult(interval){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "deleteJobResult",
		"DAYINTERVAL":interval,
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
			completedJobTable.setData(JSON.parse(dataJS, dateParserDebug));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});
}
function getCompletedJobResult(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "getCompletedJobResult",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
			completedJobTable.setData(JSON.parse(dataJS, dateParserDebug));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});
}
function getWorkQueueLengths(){
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		var queueLengths = JSON.parse(dataJS);
		workQueueHTML="<table border=\"1\"><tr><td style=\"width:250px\" align=\"left\"><b>Queue</b></td><td style=\"width:70px\" align=\"center\"><b>Length</b></td></tr>";
		workQueueHTML=workQueueHTML+"<tr><td align=\"left\">Email Queue</td><td align=\"center\">"+queueLengths[0]+"</td></tr>";
		workQueueHTML=workQueueHTML+"<tr><td align=\"left\">Report Queue</td><td align=\"center\">"+queueLengths[1]+"</td></tr>";
		workQueueHTML=workQueueHTML+"<tr><td align=\"left\">Monte Carlo Queue</td><td align=\"center\">"+queueLengths[2]+"</td></tr>";
		workQueueHTML=workQueueHTML+"</table>";
		workQueuesPane.markForRedraw();
	},
	actionURL : "./getWorkQueueLengths"
	});	
}
function init(){
		isc.RestDataSource.create({
		    ID: "allUsersDS",
		    fields: [{
		        name: "name",
		        title: "Name"
		    },{
		    	name:"email",
		    	title:"Email"
		    },{
		    	name:"compoundName",
		    	title:"Compound Name"
		    },{
		    	name:"allowLogon",
		    	title:"Allow Logon",
		    	type:"boolean"
		    }
		],
		   fetchDataURL: "./getAllUsersDS"
		});
		repMap = new Map();
		constructRepMgrApp();
		getRepositories();
		refresh();
		PeopleGrid.fetchData({nocache:Math.random()});
		try {
		//	setInterval(refresh, 10000);
		} catch (e){
			alert (e.message);
		}
}
function refreshServerControl(){
	
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
			QueueGrid.setData(JSON.parse(dataJS));
		} catch (e){alert (e.message);}
	},
	actionURL : "./getWorkQueueLengths"
	});	
	isc.RPCManager.sendRequest( {
		evalResult : false,
		params : {
		"OPERATION" : "getTransactionUsage",
		"NONCACHE":Math.random()
	},
	callback : function(rpcResponse, dataJS, rpcRequest) {
		try {
			TransactionGrid.setData(JSON.parse(dataJS));
		} catch (e){alert (e.message);}
	},
	actionURL : "./sessionControl"
	});	
	
	getSessions();
	
	
}
function refresh(){
	getSessions();
	getScheduledJobs();
	getCompletedJobResult();
	refreshServerControl();
}

// Patch tofix HTMLFlow Problem in SmarClient 8.0

if (window.isc != null) {
	if (isc.version.startsWith("SC_SNAPSHOT-2011-01-05/")) {
		if (isc.HTMLFlow != null) {
			isc.HTMLFlow.addProperties({modifyContent:function () {
				//
			}});
		}
	}
}

// End of Patch
var workQueueHTML;
init();
