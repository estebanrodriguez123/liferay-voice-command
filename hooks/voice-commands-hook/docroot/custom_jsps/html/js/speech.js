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
	var SPEECH_ACTIVE = 'speech-active';
	
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
			var self = this;
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
						A.speechrecognition.toggleRecognition(A.speechrecognition.isActive());
					};
					
					recognition.onerror = function (event) {
						if (event.error == 'audio-capture') {
							A.speechrecognition.toggleRecognition(false);
							ignore_onend = true;
							localStorage.setItem(SPEECH_ACTIVE, 0);
						}
						if (event.error == 'not-allowed') {
							ignore_onend = true;
							localStorage.setItem(SPEECH_ACTIVE, 0);
						}
						recognition.abort();
						try {
							recognition.start();
						} catch(err) {};
					};
					
					recognition.onend = function () {
						A.speechrecognition.start();
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
						A.speechrecognition.executeCommand(final_transcript, self.get('urlParamOptions'));
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
			},
			urlParamOptions: {
				value: {}
			}
		}
	});
	
	A.speechrecognition.hidePopMessage = function () {
		A.one(speechresults).transition({
			easing: 'ease-out',
			duration: 1,
			opacity: 0
		});    
	};
	
	A.speechrecognition.showPopMessage = function () {
		A.one(speechresults).transition({
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
	
	A.speechrecognition.upgrade = function () {
		start_button.style.visibility = 'hidden';
	};
	
	A.speechrecognition.linebreak = function (s) {
		return s.replace(two_line, '<p></p>').replace(one_line, '<br>');
	};
	
	A.speechrecognition.isActive = function() {
		var speech_active = false;
		var object = localStorage.getItem(SPEECH_ACTIVE);
		if (object !== null && recognizing) {
			return (Number(object) === 1);
		}
		return false;
	};
	
	A.speechrecognition.toggleRecognition = function(toggle) {
		var isActive = !A.speechrecognition.isActive();
		if (typeof toggle === 'boolean') {
			isActive = toggle;
		}
		localStorage.setItem(SPEECH_ACTIVE, (isActive ? 1 : 0));
		A.one('.speech button').toggleClass('inactive', !isActive);
		start_img.src = (isActive) ? MIC_ANIMATE : MIC_SLASH;
	};
	
	A.speechrecognition.startButton = function (event) {
		A.speechrecognition.toggleRecognition();
	};
	
	A.speechrecognition.start = function () {
		final_transcript = '';
		recognition.lang = 'en-US';
		recognition.start();
		ignore_onend = false;
		final_span.innerHTML = '';
		interim_span.innerHTML = '';
		A.speechrecognition.toggleRecognition(A.speechrecognition.isActive());
	};
	
	A.speechrecognition.executeCommand = function (speechCommand, urlParamOptions) {
		if (speechCommand === 'start') {
			A.speechrecognition.toggleRecognition(true);
			return;
		}
		if (speechCommand === 'stop') {
			A.speechrecognition.toggleRecognition(false);
			return;
		}
		if (!A.speechrecognition.isActive()) {
			return;
		}
		var redirect = A.speechrecognition.readCustomCommands(speechCommand);
		if (redirect != null && redirect.length > 0) {
			redirect = A.Lang.sub(redirect, urlParamOptions)
			window.location.assign(redirect);
		}
	};
	
	A.speechrecognition.readCustomCommands = function (speechCommand) {
		var result = null;
		var keyWordLength = keyWord.length;
		var indexOf = speechCommand.indexOf(keyWord);
		
		if (indexOf > -1 && keyWordLength > 0) {
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
