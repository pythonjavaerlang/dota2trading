/*
* jquery.dota2in
*
* Contains utilities for dota2in application
*
* DEPENDANCIES:
* 
* jquery.alerts
* jquery.ui
* jsi18n for gettext()
*
* 
*
*/
function gettext(msg){return msg;}; //later
(function($){
	$.dota2in = {
		    /* 
		    * Having a lot of  $.fn.<method_name> = function(){} statements
		    * clutters up the $.fn namespace. To remedy this, we should collect 
		    * all of our methods in an object literal and call them by passing 
		    * the string name of the method to the plugin. 
		    */
		    main_class_name: 'main-content', // class, dialog positions calculated from
		    trim: function (txt, len){
			    /*
			    * trims text to specified length and adds '..'
			    */
		            if(txt.length>len) return txt.substr(0,len)+'..';
		            return txt;
		    },
		    strip: function (txt){
			    /*
			    * Return a copy of the string S with leading and trailing
			    * whitespace removed.
			    */
		            if(!txt) return '';
		            var m = [[/&/g, "&amp;"], [/</g, "&lt;"], [/>/g, "&gt;"], [/"/g, "&quot;"]]
		            for(i in m) txt=txt.replace(m[i][0], m[i][1]);
		            return txt;
		    },
		    shit_happened: function(msg){
			var message = gettext("Error happened. Reload page ?");
			if(msg!=undefined){
			    message = msg;
			}
			jConfirm(message, gettext("Confirm"),function(r){
			    if(r==true){
				window.location.reload(true);
			    }
			},gettext("yes"), gettext("no"));
		    },
		    pad: function(number,length) {
			    /*
			    * adds leading zeroes
			    */
		            var str = '' + number;
		            while (str.length < length) str = '0' + str;
		            return str;
		    },
		    style_radio: function (callback){
			    /*
			    * Applies style to radio buttons
			    *
			    * TODO: to allow user to uncheck all radio buttons
			    */
		            $('span[id^=id_radio_button_]').unbind('click');
		            $('span[id^=id_radio_button_]').data('callback', callback);
		            $('input[type="radio"]').each(function(){
		                $(this).hide();
		                var label = this.parentNode;
		                var input_id=this.id;
		                var txt=$('span#label_'+input_id).text(); //this.value;
		                $('span#label_'+input_id).hide();
		                $('<span class="radio_button" id="id_radio_button_'+input_id+'">'+txt+'</span>').prependTo(label);

		                if($(this).attr('checked'))  $(this).parent().find('.radio_button').removeClass( "radio_normal" ).addClass( "radio_highlighted");

		                $('span[id^=id_radio_button_]').click(function(){
		                    var inputs = $('input[type="radio"]');
		                    var group = this.nextSibling.name;
		                    for(a = 0; a < inputs.length; a++) {
		                        if(inputs[a].name == group) {
		                            $(inputs[a].previousSibling).removeClass( "radio_highlighted" ).addClass( "radio_normal");
		                        }
		                    }
		                    $(this).removeClass( "radio_normal" ).addClass( "radio_highlighted");
		                    var callback = $(this).data('callback');
		                    if(callback!=undefined) callback(this.id);
		                });

		                $('input[type="radio"]').change(function(){
		                    var inputs = $('input[type="radio"]');
		                    var group = this.name;
		                    for(a = 0; a < inputs.length; a++) {
		                        if(inputs[a].name == group) {
		                            $(inputs[a].previousSibling).removeClass( "radio_highlighted" ).addClass( "radio_normal");
		                        }
		                    }
		                    $(this.previousSibling).removeClass( "radio_normal" ).addClass( "radio_highlighted");
		                });

		            });
		    },
		    style_checkboxes: function(callback){
			    /*
			    * Applies style to radio buttons
			    *
			    * TODO: to allow user to uncheck all radio buttons
			    */
		            $('input[type=checkbox]').each(function(){
		                $(this).hide();
		                if($(this).attr('checked')) $('#'+this.id+'_checkbox').addClass( "checkbox_checked" );
		                var input_id = this.id;

		                $('#'+input_id+'_checkbox').unbind('click');
		                $('#'+input_id+'_checkbox').data('callback', callback);

		                $('#'+input_id+'_checkbox').click(function(){
		                    if($('#'+input_id).attr('checked')=='checked'){
		                        $('#'+input_id+'_checkbox').removeClass( "checkbox_checked");
		                        $('#'+input_id).attr('checked', false);
		                    }else{
		                        $('#'+input_id+'_checkbox').addClass( "checkbox_checked" );
		                        $('#'+input_id).attr('checked', true);
		                    }
		                    var callback = $(this).data('callback');
		                    if(callback!=undefined) callback(input_id);
		                });
		            });
		    },
		    rpc: function(name, url_args, data, callback){
			/*
			*
			* 1. Takes RPC URL by "name",
			* 2. Replaces any variables in URL with values, taken from "url_args"
			* 3. Performs $.getJSON(URL) call, sending "data"
			* 4. If response was not "failure", calls "callback" after response 
			*    received from server
			*
		        */
		        var url = $.rpc_functions[name];
			if(url==undefined) {
			    $.error("No such RPC function found "+name);
			    return;
			}
			url = $.fn.dota2in('template', url, url_args);
			$.fn.dota2in('get_json', url, data, callback);
		    },
		    conditional_rpc: function(name, url_args, data, callback, final_callback){
			/*
			* Do the same as $.fn.dota2in('rpc'),
			* but asks user a question if he really indended
			* to perform the call
			*/
			jConfirm(gettext("Are you sure?"), gettext("Confirm"),function(r){
			    if(r==true){
				$.fn.dota2in('rpc', name, url_args, data, callback);
			    }
			},gettext("yes"), gettext("no"));
			if(final_callback) final_callback();
		    },
		    get_json: function(url, data, callback){
		     $.ajax({
			url: url,
			cache: false,
			dataType: 'json',
			data: data,
			success: function(data, status){
			    if(data=='failure'){
				$.fn.dota2in('shit_happened');
				return;
			    } else {
				$('#failure').hide();
			    }
			    if(callback) callback(data, status);
			}
		     }).fail(function(jqxhr, textStatus, error){
			var err = textStatus + ', ' + error + '. Reload page ?';
			if(error!='') $.fn.dota2in('shit_happened', err);
		     });
		    },
		    template: function(template, data) {
		    	/*
		    	* small templating engine
		    	*
		    	* Usage example:
		    	* $.fn.dota2in("<p>Hello {user.first_name} {user.last_name}! Your account is <strong>{user.account.status}</strong></p>", data) 
		    	* 
		    	* Where data is the following:
		    	* data= {
		    	*  user: {
		    	*    login: "nobody",
		    	*    first_name: "John",
		    	*    last_name: "Doe",
		    	*    account: {
		    	*      status: "active",
		    	*      expires_at: "2012-01-01"
		    	*    }
		    	*  }
		    	* }  
		    	*
		    	*/
		    	return template.replace(/\{([\w\.]*)\}/g, function (str, key) {
		    	    var keys = key.split("."), value = data[keys.shift()];
		    	    $.each(keys, function () { value = value[this]; });
		    	      return (value === null || value === undefined) ? "" : value;
		            });
		        },
		        fill_form_fields: function(data, prefix){
		    	if(prefix==undefined)prefix='';
		    	else prefix = prefix+'_';
		    	for(k in data){
		    	    if($('#id_'+prefix+k).attr('type')=='checkbox') {
		        	        if(data[k]==true){
		    		    if($('#id_'+prefix+k).attr('checked')!='checked') $('#id_'+prefix+k+'_checkbox').click();
		    		} else {
		    		    if($('#id_'+prefix+k).attr('checked')=='checked') $('#id_'+prefix+k+'_checkbox').click();
		    		}
		    	    } else if($('#id_'+prefix+k).tagName=='SELECT') $('#id_'+prefix+k+' option[value='+data[k]+']').attr('selected', 'selected');
		    	    else $('#id_'+prefix+k).val(data[k]);
		    	}
		        },
		        setDialogPosition: function(action, center){
		    	/*
		            * Calculates relative position of dialog
		    	*/
		          var xScroll, yScroll;
		          if (window.innerHeight && window.scrollMaxY) {
		              xScroll = document.body.scrollWidth;
		              yScroll = window.innerHeight + window.scrollMaxY;
		          } else if (document.body.scrollHeight > document.body.offsetHeight){ // all but Explorer Mac
		              xScroll = document.body.scrollWidth;
		              yScroll = document.body.scrollHeight;
		          } else if (document.documentElement && document.documentElement.scrollHeight > document.documentElement.offsetHeight){ // Explorer 6 strict mode
		              xScroll = document.documentElement.scrollWidth;
		              yScroll = document.documentElement.scrollHeight;
		          } else { // Explorer Mac...would also work in Mozilla and Safari
		              xScroll = document.body.offsetWidth;
		              yScroll = document.body.offsetHeight;
		          }

		          if (self.innerHeight) { // all except Explorer
		              windowWidth = self.innerWidth;
		              windowHeight = self.innerHeight;
		          } else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
		              windowWidth = document.documentElement.clientWidth;
		              windowHeight = document.documentElement.clientHeight;
		          } else if (document.body) { // other Explorers
		              windowWidth = document.body.clientWidth;
		              windowHeight = document.body.clientHeight;
		          }

		          // for small pages with total height less then height of the viewport
		          if(yScroll < windowHeight){
		              pageHeight = windowHeight;
		          } else {
		              pageHeight = yScroll;
		          }

		          // for small pages with total width less then width of the viewport
		          if(xScroll < windowWidth){
		              pageWidth = windowWidth;
		          } else {
		              pageWidth = xScroll;
		          }

		          var leftOffsetOfElement=$('#dialog').data('leftOffsetOfElement');
		          var topOffsetOfElement=$('#dialog').data('topOffsetOfElement');
		          var heightOfSelectedElement=$('#dialog').data('heightOfSelectedElement');
		          var widthOfSelectedElement=$('#dialog').data('widthOfSelectedElement');

		          var scrollTop = ($(document).scrollTop());
		          var scrollLeft = ($(document).scrollLeft());
		          var scrollBottom = pageHeight-scrollTop-windowHeight;
		          var scrollRight = pageWidth-scrollLeft-windowWidth;
		          var bottomOffsetOfElement = pageHeight-topOffsetOfElement-heightOfSelectedElement;
		          var rightOffsetOfElement = pageWidth-leftOffsetOfElement-widthOfSelectedElement;
		          var topDistanceToWindowBorder = topOffsetOfElement-scrollTop;
		          var leftDistanceToWindowBorder = leftOffsetOfElement-scrollLeft;
		          var rightDistanceToWindowBorder = rightOffsetOfElement-scrollRight;
		          var bottomDistanceToWindowBorder = bottomOffsetOfElement-scrollBottom;
		          var topDistanceToDocumentBorder = topOffsetOfElement;
		          var leftDistanceToDocumentBorder = leftOffsetOfElement;
		          var rightDistanceToDocumentBorder = pageWidth-(leftOffsetOfElement+widthOfSelectedElement);
		          var bottomDistanceToDocumentBorder = pageHeight-(topOffsetOfElement+heightOfSelectedElement);
		          var dialogHeight = $('.ui-dialog').height();
		          var dialogWidth = $('.ui-dialog').width();
		          var arrowHeight = 14;
		          var arrowWidth = 27;
		          var dialogTop=dialogLeft=0;
		          var mainContentOffsetLeft = $('.'+$.dota2in.main_class_name).offset().left;
		          var mainContentWidth = $('.'+$.dota2in.main_class_name).width();
		          var minSpaceToTheCenterOfArrow = 43;

		          if( center ){
		            if( dialogHeight > windowHeight ){
		                dialogTop = scrollTop;
		            }else{
		                dialogTop = scrollTop + (windowHeight-dialogHeight)/2;
		            }

		            if( dialogWidth > windowWidth ){
		                dialogLeft = scrollLeft;
		            }else{
		                dialogLeft = (windowWidth-dialogWidth)/2;
		            }
		          } else {
		            if(topDistanceToWindowBorder > bottomDistanceToWindowBorder){
		                if(topDistanceToWindowBorder > dialogHeight+arrowHeight){
		                    dialogTop = topOffsetOfElement - dialogHeight - arrowHeight;
		                    if( action == 'open' ){
		                        ($('<div></div>')).addClass('bg_arrow_bottom').appendTo($('.ui-dialog'));
		                        $('.ui-dialog').css('padding-top', '8px');
		                    } else {
		                        var currentDialogPosition = 'top';
		                    }
		                    var arrowClass = '.bg_arrow_bottom';
		                } else {
		                    dialogTop = topOffsetOfElement+heightOfSelectedElement;
		                    if( action == 'open' )
		                    {
		                        ($('<div></div>')).addClass('bg_arrow').prependTo($('.ui-dialog'));
		                        $('.ui-dialog').css('padding-top', '0');
		                    }
		                    else {
		                        var currentDialogPosition = 'bottom';
		                    }
		                    var arrowClass = '.bg_arrow';
		                }
		            } else {
		                dialogTop = topOffsetOfElement+heightOfSelectedElement;
		                if( action == 'open' ) {
		                    ($('<div></div>')).addClass('bg_arrow').prependTo($('.ui-dialog'));
		                    $('.ui-dialog').css('padding-top', '0');
		                } else {
		                    var currentDialogPosition = 'bottom';
		                }
		                var arrowClass = '.bg_arrow';
		            }

		            if(action == 'resize'){
		                var checkPositionResult = checkPositionFlag(currentDialogPosition);
		                if( checkPositionResult == 'reverse' ) {
		                    if( arrowClass=='.bg_arrow' ) {
		                        $('.ui-dialog').find('.bg_arrow_bottom').remove();
		                        ($('<div></div>')).addClass('bg_arrow').prependTo($('.ui-dialog'));
		                        $('.ui-dialog').css('padding-top', '0');
		                    }
		                    else {
		                        $('.ui-dialog').find('.bg_arrow').remove();
		                        ($('<div></div>')).addClass('bg_arrow_bottom').appendTo($('.ui-dialog'));
		                        $('.ui-dialog').css('padding-top', '8px');
		                    }
		                }
		            }

		            var arrowPoint = leftOffsetOfElement+widthOfSelectedElement/2;
		            var arrowShiftValue = 0;

		            if( dialogWidth > mainContentWidth ) {
		                dialogLeft = mainContentOffsetLeft;
		                arrowShiftValue = arrowPoint-dialogLeft-30-arrowWidth/2;
		            } else {
		                var rightSpaceBetweenCenterOfElementAndMainContent = (mainContentOffsetLeft+mainContentWidth) - (leftOffsetOfElement+widthOfSelectedElement/2);
		                if( rightSpaceBetweenCenterOfElementAndMainContent > dialogWidth-minSpaceToTheCenterOfArrow )
		                {
		                    dialogLeft = arrowPoint-minSpaceToTheCenterOfArrow;
		                    arrowShiftValue = 0;
		                }
		                else {
		                    dialogLeft = mainContentOffsetLeft+mainContentWidth-dialogWidth;
		                    arrowShiftValue = arrowPoint-dialogLeft-30-arrowWidth/2;
		                }
		            }
		            $(arrowClass).css('background-position',arrowShiftValue+'px'+' 50%');
		          }

		          $('.ui-dialog').css({'top':dialogTop,'left':dialogLeft});
		          $('.ui-dialog').css('z-index', '1002');
		        },
		        open_dialog: function(posX, posY, header, data, height){
		    	/*
		    	* Displays dialog box with arrow, pointing to clicked link 
		    	*/
		    	if($('div#dialog').length>0)return false;
		    	$('body').append('<div id="dialog"></div>');
		    	var w = $('#dialog');
		    	w.dialog({title: '', zindex: 20, modal: true, resizable: false,
		    	 draggable: false, width:"700",
		    	 beforeclose: function(){
		    	  $('.ui-dialog').remove();
		    	  $('#dialog').remove();
		    	 }
		    	});
		    	if(height) w.dialog('option', 'height', height);

		    	$('#ui-dialog-title-dialog').append(header);
		    	($('<div></div>')).addClass('ui-dialog-header').appendTo($('.ui-dialog-titlebar'));
		    	($('<div></div>')).addClass('ui-dialog-header-leftbg').appendTo($('.ui-dialog-header'));
		    	($('<div></div>')).addClass('ui-dialog-header-rightbg').appendTo($('.ui-dialog-header-leftbg'));
		    	$('#ui-dialog-title-dialog').appendTo($('.ui-dialog-header-rightbg'));
		    	$('.ui-dialog-titlebar-close').appendTo($('.ui-dialog-header-rightbg'));

		    	($('<div></div>')).addClass('bg_header_left').prependTo($('.ui-dialog'));
		    	($('<div></div>')).addClass('bg_header_right').appendTo($('.bg_header_left'));
		    	$('.ui-dialog-titlebar').appendTo($('.bg_header_right'));
		    	($('<div></div>')).addClass('bg_body_left').appendTo($('.ui-dialog'));
		    	($('<div></div>')).addClass('bg_body_right').appendTo($('.bg_body_left'));
		    	($('<div></div>')).addClass('dialog_content_border').appendTo($('.bg_body_right'));
		    	$('.ui-dialog-content').appendTo($('.dialog_content_border'));
		    	($('<div></div>')).addClass('dialog-content-cornerl').appendTo($('.dialog_content_border'));
		    	($('<div></div>')).addClass('dialog-content-cornerr').appendTo($('.dialog-content-cornerl'));
		    	($('<div></div>')).addClass('bg_footer_left').appendTo($('.ui-dialog'));
		    	($('<div></div>')).addClass('bg_footer_right').appendTo($('.bg_footer_left'));
		    	($('<div></div>')).addClass('ui-dialog-footer').appendTo($('.bg_footer_right'));

		    	//$('<div class="bg_arrow"></div>').appendTo($('.ui-dialog'));

		    	 $('.ui-dialog').css('z-index', '9999');
		    	 $('.ui-dialog').css('top', posY);
		    	 $('.ui-dialog').css('left', posX);
		    	 w.append(data);
		    	 w.dialog('open');
		    	 return w;
		        },
		        open_big_dialog: function(posX, posY, header, data, options){
		        	/*
		        	* Displays big dialog on center of screen
		        	*/
		        	$('body').append('<div id="dialog"></div>');

		        	var w = $('#dialog');
		        	var dialog_options={
		        	 title: '', zindex: 20, modal: true, resizable: false,
		        	 draggable: false, width:"auto",
		        	 beforeclose: function(event, ui){
		        	  $('.ui-dialog').remove();
		        	  $('#dialog').remove();
		        	 }
		        	}
		        	if(options){
		        	    for(k in options){
		        		dialog_options[k] = options[k];
		        	    }
		        	}
		        	w.dialog(dialog_options);

		        	$('#ui-dialog-title-dialog').append(header);
		        	($('<div></div>')).addClass('bg_header_left').prependTo($('.ui-dialog'));
		        	($('<div></div>')).addClass('bg_header_right').appendTo($('.bg_header_left'));
		        	$('.ui-dialog-titlebar').appendTo($('.bg_header_right'));

		        	($('<div></div>')).addClass('ui-dialog-header').appendTo($('.ui-dialog-titlebar'));
		        	($('<div></div>')).addClass('ui-dialog-header-leftbg').appendTo($('.ui-dialog-header'));
		        	($('<div></div>')).addClass('ui-dialog-header-rightbg').appendTo($('.ui-dialog-header-leftbg'));
		        	$('#ui-dialog-title-dialog').appendTo($('.ui-dialog-header-rightbg'));
		        	$('.ui-dialog-titlebar-close').appendTo($('.ui-dialog-header-rightbg'));

		        	($('<div></div>')).addClass('bg_body_left').appendTo($('.ui-dialog'));
		        	$('.bg_body_left').addClass('big_dialog_content');
		        	($('<div></div>')).addClass('bg_body_right').appendTo($('.bg_body_left'));
		        	$('.ui-dialog-content').appendTo($('.bg_body_right'));
		        	($('<div></div>')).addClass('bg_footer_left').appendTo($('.ui-dialog'));
		        	($('<div></div>')).addClass('bg_footer_right').appendTo($('.bg_footer_left'));
		        	($('<div></div>')).addClass('ui-dialog-footer').appendTo($('.bg_footer_right'));

		        	//($('<div></div>')).addClass('bg_arrow').prependTo($('.ui-dialog'));
		        	//$('<div class="bg_arrow"></div>').prependTo($('.ui-dialog'));

		        	 $('.ui-dialog').css('z-index', '9999');
		        	 $('.ui-dialog').css('top', '0');
		        	 w.append(data);
		        	 w.dialog('open');

		        	 return w;
		            },
		            do_eval: function(msg){
		             try {
		              var obj = $.parseJSON(msg);
		              return obj;
		             } catch(e) {
		              if(e.name!='ReferenceError');
		              $.fn.dota2in('shit_happened');
		             }
		             return {};
		            },
		            big_dialog_form: function(e, title, edit_title, rpc_name, url_args, form_id, fields, afterOpenCallback, beforeCloseCallback, beforeSubmitCallback){
		        	/*
		        	* Opens dialog, pointing on link clicked
		        	* Displays form
		        	*
		        	* Arguments:
		        	* "title" -- dialog title, when webform must be shown for new object
		        	* "edit_title" -- dialog title for object that should be edited
		        	* "url" -- URL for post
		        	* "form_id" -- ID of form
		        	* "fields" -- associative array with labels and input fields
		        	*	      Format:
		        	*		[{
		        	*		  "name": "phone",
		        	*		  "value": "",
		        	*		  "label": "Phone"
		        	*		  "type": "text",
		        	*		  "required": true,
		        	*		},
		        	*		{
		        	*		  "name": "type",
		        	*		  "value": "mobile",
		        	*		  "label": "Phone Type"
		        	*		  "type": "select",
		        	*		  "choices": [{"name": "Business", "value": "business"}, {"name": "Mobile", "value": "mobile"}]
		        	*		},
		        	*		{
		        	*		  "name": "is_primary",
		        	*		  "value": true,
		        	*		  "label": "Primary Phone"
		        	*		  "type": "checkbox",
		        	*		},
		        	*		{
		        	*		  "name": "the_id",
		        	*		  "value": "1",
		        	*		  "type": "hidden",
		        	*		},
		        	*		]
		        	*/
		                var this_class=$(e).attr('class');
		                var posX=$(e).offset().left;
		                var posY=$(e).offset().top;
		                var currentElement=$(e);
		        	var button_label=gettext("Save");
		        	var is_edit_form=true;
		        	var header=title;
		        	if(title.length==0){
		        	    button_label=gettext("Edit");
		        	    is_edit_form=false;
		        	    header=edit_title;
		        	}

		                var url = $.rpc_functions[rpc_name];
		        	if(url==undefined){
		        	    $.error("No such RPC function found "+name);
		        	    return;
		        	}
		        	url = $.fn.dota2in('template', url, url_args);

		        	var fields_text = '';
		        	for(var i=0;i!=fields.length;i++){
		        	    if(!fields[i].name) $.error("'name' attribute not found");
		        	    if(fields[i].type=='hidden') {
		        		fields_text+='<input type="hidden" name="'+fields[i].name+'" id="id_'+form_id+'_'+fields[i].name+'" value="'+fields[i].value+'">';
		        		continue;
		        	    }
		        	    fields_text+='<p><label for="id_'+form_id+'_'+fields[i].name+'">'+fields[i].label+(fields[i].required==true?'<span style="color:red;">*</span>':'')+'</label>';
		        	    if(fields[i].type=='text') fields_text+='<input type="text" name="'+fields[i].name+'" value="'+(fields[i].value==undefined?'':fields[i].value)+'" id="id_'+form_id+'_'+fields[i].name+'"/>';
		        	    else if(fields[i].type=='select'){
		        		fields_text+='<select name='+fields[i].name+' id="id_'+form_id+'_'+fields[i].name+'">';
		        		for(var j=0;j!=fields[i].choices.length;j++){
		        		    fields_text+='<option value="'+fields[i].choices[j].value+'"'+(fields[i].value==fields[i].choices[j].value?' selected="selected"':"")+'>'+fields[i].choices[j].name+'</option>';
		        		}
		        		fields_text+='</select>';
		        	    } else if(fields[i].type=='checkbox'){
		        		fields_text+='<input type="checkbox" name="'+fields[i].name+'"'+(fields[i].value==true?' checked="checked"':'')+' id="id_'+form_id+'_'+fields[i].name+' class="checkbox"/> \
		        <span class="checkbox" id="id_'+form_id+'_'+fields[i].name+'_checkbox"></span>';
		        	    } else if(fields[i].type=='password'){
		        		fields_text+='<input type="password" name="'+fields[i].name+'" id="id_'+form_id+'_'+fields[i].name+'" />';
		        	    }
		        	    fields_text+='<span class="err" id="'+form_id+'_'+fields[i].name+'_errors"></span>';
		        	}

		        	var big_form = $.fn.dota2in('open_big_dialog', posX+20, posY, header, '<form action="'+url+'" name="'+form_id+'_form" id="'+form_id+'_form" method="post">'+fields_text+' \
		        <button type="submit" id="submit_'+form_id+'">'+button_label+'</button></form>');

		        	$('#dialog').data('leftOffsetOfElement', posX);
		        	$('#dialog').data('topOffsetOfElement', posY);
		        	$('#dialog').data('heightOfSelectedElement', currentElement.height());
		        	$('#dialog').data('widthOfSelectedElement', currentElement.width());
		        	$.fn.dota2in('setDialogPosition', 'open', true);

		        	$.fn.dota2in('style_checkboxes');

		                $('form[name='+form_id+'_form]').find('input[type=text]').first().focus();

		                if(afterOpenCallback) afterOpenCallback();

		        	big_form = $('form[name='+form_id+'_form]').ajaxForm({
		        	    success: function(msg){
		        	     $('.err').empty();
		        	     if(typeof msg == "string") msg=$.fn.dota2in('do_eval', msg);
		        	     if(msg.errors){
		        	       $('#submit_'+form_id).removeAttr("disabled");
		        	       for(k in msg.errors) $('#'+form_id+'_'+k+'_errors').append(msg.errors[k]);
		        	     } else {
		        	      if(beforeCloseCallback) beforeCloseCallback(msg);
		        	      $('.ui-dialog-titlebar-close').click();
		        	     }
		        	    }
		        	});
		                $('#submit_'+form_id).click(function(){
		                  $('#submit_'+form_id).attr("disabled","disabled");

		        	  if(beforeSubmitCallback){
		        	     var data_to_submit = {};
		          	     for(var i=0;i!=fields.length;i++){
		        		data_to_submit[fields[i].name] = $('#id_'+form_id+'_'+fields[i].name).val();
		        	     }
		        	     beforeSubmitCallback(data_to_submit);
		        	  }

		                  big_form.submit();
		                  return false;
		                });
		                return false;
		            },
		            normalize_decimal: function(value){
		                var result=parseFloat(value).toFixed(2).replace(/(\.[0-9]*?)0+$/, "$1");
		                result=result.replace(/\.$/, "");
		                return result;
		              },
		              hash_code: function(string){
		                  var hash = 0, i, char;
		                  if (string.length == 0) return hash;
		                    for (i = 0, l = string.length; i < l; i++) {
		                      char  = string.charCodeAt(i);
		                      hash  = ((hash<<5)-hash)+char;
		                      hash |= 0; // Convert to 32bit integer
		                    }
		                  return hash;
		                },
}

$.fn.dota2in = function(method){

	    // Method calling logic
	    if ( $.dota2in[method] ) {
	      return $.dota2in[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
	    } else if ( typeof method === 'object' || ! method ) {
	      return $.dota2in.init.apply( this, arguments );
	    } else {
	      $.error( 'Method ' +  method + ' does not exist on jQuery.dota2in' );
	    }

}
})(jQuery);
