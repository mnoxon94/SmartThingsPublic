/**
 *  iPhone1
 *
 *  Copyright 2016 Michael Noxon
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Toggle Presence Device",
    namespace: "mnoxon94",
    author: "Michael Noxon",
    description: "Receive a HTTP request to toggle a presence device like a iPhone",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


/**
 *  Away/Here Presence for HTTP GET
 *
 */

preferences {
    section(title: "Select Devices") {
        input "light", "capability.switch", title: "Select Phone", required: true, multiple:false
        input "phone", "phone", title: "Phone Number (for SMS, optional)", required: true
    }
    
}

def snd_sms(){
    DEBUG("sending SMS")
    sendSms(phone, "${light.name} - https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/iPhone/here?access_token=${state.accessToken}")
    sendSms(phone, "${light.name} - https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/iPhone/away?access_token=${state.accessToken}")
	DEBUG("${phone} - ${light.name} - https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/iPhone/here?access_token=${state.accessToken}")
	DEBUG("${phone} - ${light.name} - https://graph.api.smartthings.com/api/smartapps/installations/${app.id}/iPhone/away?access_token=${state.accessToken}")
}


def installed() {
	createAccessToken()
	getToken()
	DEBUG("Installed Phone with rest api: $app.id")
    DEBUG("Installed Phone with token: $state.accessToken")
    DEBUG("Installed with settings: $light.name")
}
def updated() {
	DEBUG("Updated Phone with rest api: $app.id")
    DEBUG("Updated Phone with token: $state.accessToken")
}


mappings {
  path("/iPhone/here") {
    action: [
      GET: "updateSwitchOn"
    ]
  }
  path("/iPhone/away") {
    action: [
      GET:"updateSwitchOff"
    ]
  }
}


// Callback functions
def getSwitch() {
    // This returns the current state of the switch in JSON
    return light.currentState("switch")
}

def updateSwitchOn() {
        	DEBUG("on")
            light.on();
}

def updateSwitchOff() {
        	DEBUG("off")
            light.off();
}

def getToken(){
if (!state.accessToken) {
		try {
			getAccessToken()
			DEBUG("Creating new Access Token: $state.accessToken")
		} catch (ex) {
			DEBUG("Did you forget to enable OAuth in SmartApp IDE settings for SmartTiles?")
            DEBUG(ex)
		}
	}
    snd_sms()
}

private def DEBUG(message){
	log.debug message
}
