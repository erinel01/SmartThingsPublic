/**
 *  Virtual Custom Switch v1.0.20170408
 *  Copyright 2017 JZ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
	definition (name: "Virtual Custom Switch", namespace: "JZ", author: "JZ") {
		capability "Switch"
        capability "Refresh"
        attribute "refresh", "string"
	}

	tiles(scale: 2) {
		standardTile("switch", "device.switch", width: 6, height: 2, canChangeIcon: true) {
			state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}
		valueTile("customTriggered", "device.customTriggered", width: 6, height: 2, decoration: "flat") {
			state("default", label: 'Custom triggered:\r\n${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("refreshTriggered", "device.refreshTriggered", width: 4, height: 2, decoration: "flat") {
			state("default", label: 'Refreshed:\r\n${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("refresh", "device.refresh", width: 2, height: 2, decoration: "flat") {
			state "default", label:'REFRESH', action: "refresh", icon: "st.secondary.refresh-icon", backgroundColor:"#53a7c0", nextState: "refreshing"
			state "refreshing", label: 'REFRESHING', action: "refresh", icon: "st.secondary.refresh-icon", backgroundColor: "#FF6600", nextState: "default"
		}
        main "switch"
		details(["switch","on","off","customTriggered","refreshTriggered","refresh"])
	}
}

def refresh() {
	log.debug "refresh()"
	sendEvent(name: "refresh", value: new Date().format("yyyy-MM-dd h:mm:ss a", location.timeZone))
}

def parse(description) {
	def eventMap
	if (description.type == null) eventMap = [name:"$description.name", value:"$description.value"]
	else eventMap = [name:"$description.name", value:"$description.value", type:"$description.type"]
	createEvent(eventMap)
}

def on() {
	log.debug "$version on()"
	sendEvent(name: "switch", value: "on")
}

def off() {
	log.debug "$version off()"
	sendEvent(name: "switch", value: "off")
}

private getVersion() {
	"PUBLISHED"
}