/**
 * Copyright (C) 2005-2014 Rivet Logic Corporation.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

AUI().add('speechrecognition', function (A) {
		var two_line = /\n\n/g;
    	var one_line = /\n/g;
    	var MIC = '/voice-commands-hook/image/mic.gif';
    	var MIC_ANIMATE = '/voice-commands-hook/image/mic-animate.gif';
    	var MIC_SLASH = '/voice-commands-hook/image/mic-slash.gif';
    	
        var recognition = null;

        var commandsMap = {};
        var commandsKey= [];
        var keyWord = 0;
        var userSetupComplete;

        var final_transcript = '';
        var recognizing = false;
        var ignore_onend = false;

        A.namespace('speechrecognition');

        A.speechrecognition.SpeechRecognition = A.Base.create('speech-recognition', A.Base, [], {
            initializer: function () {
                commandsMap = this.get('commandsMap');
                commandsKey = this.get('commandsKey');
                keyWord = this.get('keyWord');
                userSetupComplete = this.get('userSetupComplete');

                if (userSetupComplete) {
                    if (!('webkitSpeechRecognition' in window)) {
                    	A.speechrecognition.upgrade();
                    } else {
                        start_button.style.display = 'inline-block';
                        recognition = new webkitSpeechRecognition();
                        recognition.continuous = true;
                        recognition.interimResults = false;

                        recognition.onstart = function () {
                            recognizing = true;
                            start_img.src = MIC_ANIMATE;
                        };

                        recognition.onerror = function (event) {
                            if (event.error == 'audio-capture') {
                                start_img.src = MIC;
                                ignore_onend = true;
                                localStorage.setItem("speech-active", 0);
                            }
                            if (event.error == 'not-allowed') {
                                ignore_onend = true;
                                localStorage.setItem("speech-active", 0);
                            }
                        };

                        recognition.onend = function () {
                        	var object = localStorage.getItem("speech-active");
                    		if(object != null) {
                    			speech_active = new Boolean(Number(object));
                    		}
                    		if(speech_active == false) {
                    		    recognizing = false;
                    		    if (ignore_onend) {
                    		      return;
                    		    }
                    		    start_img.src = MIC;
                    		    if (!final_transcript) {
                    		      return;
                    		    }
                    		} else {
                    			A.speechrecognition.start();
                    		}
                        };

                        recognition.onresult = function (event) {
                            var interim_transcript = '';
                            if (event.results[(event.results.length - 1)].isFinal) {
                                final_transcript = event.results[(event.results.length - 1)][0].transcript;
                            } else {
                                interim_transcript = event.results[(event.results.length - 1)][0].transcript;
                            }

                            final_transcript = final_transcript.trim().toLowerCase();
                            final_span.innerHTML = A.speechrecognition.linebreak(final_transcript);
                            interim_span.innerHTML = A.speechrecognition.linebreak(interim_transcript);
                            A.speechrecognition.executeCommand(final_transcript);
                            Liferay.fire('speech:messagearrived');
                        };

                        A.speechrecognition.start();
                    }
                }
            }
        }, {
            ATTRS: {
                commandsMap: {
                    value: null
                },
                commandsKey: {
                    value: null
                },
                keyWord: {
                    value: null
                },
                userSetupComplete: {
                    value: null
                }
            }
        });
        
        A.speechrecognition.hidePopMessage = function () {
             A.one(results).transition({
                 easing: 'ease-out',
                 duration: 1,
                 opacity: 0
             });    
         };
         
         A.speechrecognition.showPopMessage = function () {
             A.one(results).transition({
                 easing: 'ease-out',
                 duration: 1,
                 opacity: 1
             }, function() {
                 window.setTimeout(function() {
                	 A.speechrecognition.hidePopMessage();
                 }, 2000);
             });
         };
         
         Liferay.on('speech:messagearrived', function() {
        	 A.speechrecognition.showPopMessage();
         });

        A.speechrecognition.upgrate = function () {
        	 start_button.style.visibility = 'hidden';
        };

        A.speechrecognition.linebreak = function (s) {
        	return s.replace(two_line, '<p></p>').replace(one_line, '<br>');
        };

        A.speechrecognition.startButton = function (event) {
        	if (recognizing) {
                localStorage.setItem("speech-active", 0);
                recognition.stop();
                return;
            } else {
            	localStorage.setItem("speech-active", 1);
            }
        	A.speechrecognition.start();
        };

        A.speechrecognition.start = function () {
        	var speech_active = false;
            var object = localStorage.getItem("speech-active");
            if (object != null) {
                speech_active = new Boolean(Number(object));
            }
            if (speech_active == true) {
            	final_transcript = '';
                recognition.lang = 'en-US';
                recognition.start();
                ignore_onend = false;
                final_span.innerHTML = '';
                interim_span.innerHTML = '';
                start_img.src = MIC_SLASH;
            }
        };

        A.speechrecognition.executeCommand = function (speechCommand) {
        	var redirect = A.speechrecognition.readCustomCommands(speechCommand);
            if (redirect != null && redirect.length > 0) {
                window.location.assign(redirect);
            }
        };

        A.speechrecognition.readCustomCommands = function (speechCommand) {
        	var result = null;
            var keyWordLength = keyWord.length;
            var indexOf = speechCommand.indexOf(keyWord);

            if (indexOf > -1) {
                speechCommand = speechCommand.substring((keyWordLength + 1), speechCommand.length);
                speechCommand = speechCommand.trim();
            }
            for (var i = 0; i < commandsKey.length; i++) {
                if (speechCommand == commandsKey[i]) {
                    result = commandsMap[speechCommand];
                    break;
                }
            }
            return result;
        };
    },
    '1.0.0', {
        requires: ['aui-tooltip', 'node', 'node-event-simulate', 'transition']
    });