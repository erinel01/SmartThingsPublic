/**
 *  Generic HTTP Device v1.0.20170327
 *  Source code can be found here: https://github.com/JZ-SmartThings/SmartThings/blob/master/Devices/Generic%20HTTP%20Device/GenericHTTPDevice.groovy
 *  Copyright 2017 JZ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */

import groovy.json.JsonSlurper

metadata {
	definition (name: "EriNelGeneric HTTP Device", author: "EriNel", namespace:"EriNel") {
		capability "Switch"
		capability "Temperature Measurement"
		capability "Sensor"
		capability "Polling"
		capability "Refresh"
		capability "Health Check"
		attribute "cpuUsage", "string"
		attribute "spaceUsed", "string"
		attribute "upTime", "string"
		attribute "cpuTemp", "string"
		attribute "freeMem", "string"
		attribute "temperature1", "string"
        attribute "temperature2", "string"
   		command "DeviceTrigger"
		command "RebootNow"
		command "ResetTiles"
		command "ClearTiles"
	}

	preferences {
		input("DeviceIP", "string", title:"Device IP Address", description: "Please enter your device's IP Address", required: true, displayDuringSetup: true)
		input("DevicePort", "string", title:"Device Port", description: "Empty assumes port 80.", required: false, displayDuringSetup: true)
		input("DevicePath", "string", title:"URL Path", description: "Rest of the URL, include forward slash.", displayDuringSetup: true)
		input(name: "DevicePostGet", type: "enum", title: "POST or GET. POST for PHP & GET for Arduino.", options: ["POST","GET"], defaultValue: "POST", required: false, displayDuringSetup: true)
		input("UseJSON", "bool", title:"Use JSON instead of HTML?", description: "", defaultValue: false, required: false, displayDuringSetup: true)
		section() {
			input("HTTPAuth", "bool", title:"Requires User Auth?", description: "Choose if the HTTP requires basic authentication", defaultValue: false, required: true, displayDuringSetup: true)
			input("HTTPUser", "string", title:"HTTP User", description: "Enter your basic username", required: false, displayDuringSetup: true)
			input("HTTPPassword", "string", title:"HTTP Password", description: "Enter your basic password", required: false, displayDuringSetup: true)
		}
	}
	
	simulator {
	}

	tiles(scale: 2) {
		valueTile("displayName", "device.displayName", width: 6, height: 1, decoration: "flat") {
			state("default", label: '${currentValue}', backgroundColor:"#DDDDDD")
		}
		valueTile("refreshTriggered", "device.refreshTriggered", width: 5, height: 1, decoration: "flat") {
			state("default", label: 'Refreshed:\r\n${currentValue}', backgroundColor:"#ffffff")
		}
		standardTile("RefreshTrigger", "device.refreshswitch", width: 1, height: 1, decoration: "flat") {
			state "default", label:'REFRESH', action: "refresh.refresh", icon: "st.secondary.refresh-icon", backgroundColor:"#53a7c0", nextState: "refreshing"
			state "refreshing", label: 'REFRESHING', action: "ResetTiles", icon: "st.secondary.refresh-icon", backgroundColor: "#FF6600", nextState: "default"
		}
		valueTile("cpuUsage", "device.cpuUsage", width: 2, height: 2) {
			state("default", label: 'CPU\r\n ${currentValue}%',
				backgroundColors:[
					[value: 0, color: "#00cc33"],
					[value: 10, color: "#99ff33"],
					[value: 30, color: "#ffcc99"],
					[value: 55, color: "#ff6600"],
					[value: 90, color: "#ff0000"]
				]
			)
		}
		valueTile("cpuTemp", "device.cpuTemp", width: 2, height: 2) {
			state("default", label: 'CPU Temp ${currentValue}',
				backgroundColors:[
					[value: 50, color: "#00cc33"],
					[value: 60, color: "#99ff33"],
					[value: 67, color: "#ff6600"],
					[value: 75, color: "#ff0000"]
				]
			)
		}
		valueTile("spaceUsed", "device.spaceUsed", width: 2, height: 2) {
			state("default", label: 'Space Used\r\n ${currentValue}%',
				backgroundColors:[
					[value: 50, color: "#00cc33"],
					[value: 75, color: "#ffcc66"],
					[value: 85, color: "#ff6600"],
					[value: 95, color: "#ff0000"]
				]
			)
		}
		valueTile("upTime", "device.upTime", width: 2, height: 2, decoration: "flat") {
			state("default", label: 'UpTime\r\n ${currentValue}', backgroundColor:"#ffffff")
		}
		valueTile("freeMem", "device.freeMem", width: 2, height: 2, decoration: "flat") {
			state("default", label: 'Free Mem\r\n ${currentValue}', backgroundColor:"#ffffff")
		}

		valueTile("temperature1", "device.temperature1", width: 3, height: 2) {
			state("default", label:'Frys\n ${currentValue}',
				backgroundColors:[
					[value: -30, color: "#153591"],
					[value: -25, color: "#00cc33"],
					[value: -20, color: "#00cc33"],
					[value: -15, color: "#f1d801"],
					[value: -10, color: "#d04e00"],
					[value: -5, color: "#bc2323"],
					[value: 0, color: "#bc2323"]
				]
			)
		}
	    valueTile("temperature2", "device.temperature2", width: 3, height: 2) {
			state("default", label:'Rum\n ${currentValue}',
				backgroundColors:[
					[value: 0, color: "#153591"],
					[value: 5, color: "#1e9cbb"],
					[value: 10, color: "#90d2a7"],
					[value: 15, color: "#00cc33"],
					[value: 20, color: "#00cc33"],
					[value: 25, color: "#d04e00"],
					[value: 30, color: "#bc2323"]
				]
			)
		}
 		standardTile("clearTiles", "device.clear", width: 2, height: 2, decoration: "flat") {
			state "default", label:'Clear Tiles', action:"ClearTiles", icon:"st.Bath.bath9"
		}       
		standardTile("RebootNow", "device.rebootnow", width: 1, height: 1, decoration: "flat") {
			state "default", label:'REBOOT' , action: "RebootNow", icon: "st.Seasonal Winter.seasonal-winter-014", backgroundColor:"#ff0000", nextState: "rebooting"
			state "rebooting", label: 'REBOOTING', action: "ResetTiles", icon: "st.Office.office13", backgroundColor: "#FF6600", nextState: "default"
		}

	}
}

def refresh() {
	def FullCommand = 'Refresh='
	if (UseJSON==true) { FullCommand=FullCommand+"&UseJSON=" }
	runCmd(FullCommand)
}
def poll() {
	refresh()
}
def ping() {
    log.debug "ping()"
	refresh()
}
def RebootNow() {
	log.debug "Reboot Triggered!!!"
	runCmd('RebootNow=')
}
def ClearTiles() {
	sendEvent(name: "cpuUsage", value: "", unit: "")
	sendEvent(name: "cpuTemp", value: "", unit: "")
	sendEvent(name: "spaceUsed", value: "", unit: "")
	sendEvent(name: "upTime", value: "", unit: "")
	sendEvent(name: "freeMem", value: "", unit: "")
	sendEvent(name: "temperature1", value: "", unit: "")
	sendEvent(name: "temperature2", value: "", unit: "")
}
def ResetTiles() {
	sendEvent(name: "refreshswitch", value: "default", isStateChange: true)
	sendEvent(name: "rebootnow", value: "default", isStateChange: true)
	log.debug "Resetting tiles."
}

def runCmd(String varCommand) {
	def host = DeviceIP
	def hosthex = convertIPtoHex(host).toUpperCase()
	def LocalDevicePort = ''
	if (DevicePort==null) { LocalDevicePort = "80" } else { LocalDevicePort = DevicePort }
	def porthex = convertPortToHex(LocalDevicePort).toUpperCase()
	device.deviceNetworkId = "$hosthex:$porthex"
	def userpassascii = "${HTTPUser}:${HTTPPassword}"
	def userpass = "Basic " + userpassascii.encodeAsBase64().toString()

	log.debug "The device id configured is: $device.deviceNetworkId"

	def headers = [:] 
	headers.put("HOST", "$host:$LocalDevicePort")
	headers.put("Content-Type", "application/x-www-form-urlencoded")
	if (HTTPAuth) {
		headers.put("Authorization", userpass)
	}
	log.debug "The Header is $headers"

	def path = ''
	def body = ''
	log.debug "Uses which method: $DevicePostGet"
	def method = "POST"
	try {
		if (DevicePostGet.toUpperCase() == "GET") {
			method = "GET"
			path = varCommand
			if (path.substring(0,1) != "/") { path = "/" + path }
			log.debug "GET path is: $path"
		} else {
			path = DevicePath
			body = varCommand 
			log.debug "POST body is: $body"
		}
		log.debug "The method is $method"
	}
	catch (Exception e) {
		settings.DevicePostGet = "POST"
		log.debug e
		log.debug "You must not have set the preference for the DevicePOSTGET option"
	}

	try {
		def hubAction = new physicalgraph.device.HubAction(
			method: method,
			path: path,
			body: body,
			headers: headers
			)
		hubAction.options = [outputMsgToS3:false]
		log.debug hubAction
		hubAction
	}
	catch (Exception e) {
		log.debug "Hit Exception $e on $hubAction"
	}
}

def parse(String description) {
//	log.debug "Parsing '${description}'"
	def whichTile = ''
	def map = [:]
	def retResult = []
	def descMap = parseDescriptionAsMap(description)
	def jsonlist = [:]
	def bodyReturned = ' '
	def headersReturned = ' '
	if (descMap["body"]) { bodyReturned = new String(descMap["body"].decodeBase64()) }
	if (descMap["headers"]) { headersReturned = new String(descMap["headers"].decodeBase64()) }
	//log.debug "BODY---" + bodyReturned
	//log.debug "HEADERS---" + headersReturned

	if (descMap["body"]) {
		if (headersReturned.contains("application/json")) {
			def body = new String(descMap["body"].decodeBase64())
			def slurper = new JsonSlurper()
			jsonlist = slurper.parseText(body)
			//log.debug "JSONLIST---" + jsonlist."CPU"
			jsonlist.put ("Date", new Date().format("yyyy-MM-dd h:mm:ss a", location.timeZone))
		} else {
			jsonlist.put ("Date", new Date().format("yyyy-MM-dd h:mm:ss a", location.timeZone))
			def data=bodyReturned.eachLine { line ->
				if (line.contains('CPU=')) { jsonlist.put ("CPU", line.replace("CPU=","")) }
				if (line.contains('Space Used=')) { jsonlist.put ("Space Used", line.replace("Space Used=","")) }
				if (line.contains('UpTime=')) { jsonlist.put ("UpTime", line.replace("UpTime=","")) }
				if (line.contains('CPU Temp=')) { jsonlist.put ("CPU Temp",line.replace("CPU Temp=","")) }
				if (line.contains('Free Mem=')) { jsonlist.put ("Free Mem",line.replace("Free Mem=",""))  }
				if (line.contains('Temperature1=')) { jsonlist.put ("Temperature1",line.replace("Temperature1=","").replaceAll("[^\\p{ASCII}]", "°")) }
				if (line.contains('Temperature2=')) { jsonlist.put ("Temperature2",line.replace("Temperature2=","").replaceAll("[^\\p{ASCII}]", "°")) }
				if (line.contains('Refresh=Success')) { jsonlist.put ("Refresh", "Success") }
				if (line.contains('Refresh=Failed : Authentication Required!')) { jsonlist.put ("Refresh", "Authentication Required!") }
				if (line.contains('RebootNow=Success')) { jsonlist.put ("RebootNow", "Success") }
				if (line.contains('RebootNow=Failed : Authentication Required!')) { jsonlist.put ("RebootNow", "Authentication Required!") }
				//ARDUINO CHECKS
				if (line == '/Refresh=') { jsonlist.put ("Refresh", "Success") }
			}
		}
	}
	if (descMap["body"]) {
		if (jsonlist."Refresh"=="Authentication Required!") {
			sendEvent(name: "refreshTriggered", value: "Use Authentication Credentials", unit: "")
			whichTile = 'refresh'
		}
		if (jsonlist."Refresh"=="Success") {
			sendEvent(name: "refreshTriggered", value: jsonlist."Date", unit: "")
			whichTile = 'refresh'
		}
		if (jsonlist."CPU") {
			sendEvent(name: "cpuUsage", value: jsonlist."CPU".replace("=","\n").replace("%",""), unit: "")
		}
		if (jsonlist."Space Used") {
			sendEvent(name: "spaceUsed", value: jsonlist."Space Used".replace("=","\n").replace("%",""), unit: "")
		}
		if (jsonlist."UpTime") {
			sendEvent(name: "upTime", value: jsonlist."UpTime".replace("=","\n"), unit: "")
		}
		if (jsonlist."CPU Temp") {
			sendEvent(name: "cpuTemp", value: jsonlist."CPU Temp".replace("=","\n").replace("\'","°").replace("C ","C="), unit: "")
		}
		if (jsonlist."Free Mem") {
			sendEvent(name: "freeMem", value: jsonlist."Free Mem".replace("=","\n"), unit: "")
		}
		if (jsonlist."Temperature1") {
			sendEvent(name: "temperature1", value: jsonlist."Temperature1".replace("=","\n").replace("\'","°").replace("C ","C="), unit: "")
			//String s = jsonlist."Temperature"
			//for(int i = 0; i < s.length(); i++)	{
			//   int c = s.charAt(i);
			//   log.trace "'${c}'\n"
			//}
		}
		if (jsonlist."Temperature2") {
			sendEvent(name: "temperature2", value: jsonlist."Temperature2".replace("=","\n").replace("\'","°").replace("C ","C="), unit: "")
			//String s = jsonlist."Temperature"
			//for(int i = 0; i < s.length(); i++)	{
			//   int c = s.charAt(i);
			//   log.trace "'${c}'\n"
			//}
		}
		if (jsonlist."RebootNow") {
			whichTile = 'RebootNow'
		}
	}

	log.debug jsonlist

	//RESET THE DEVICE ID TO GENERIC/RANDOM NUMBER. THIS ALLOWS MULTIPLE DEVICES TO USE THE SAME ID/IP
	device.deviceNetworkId = "ID_WILL_BE_CHANGED_AT_RUNTIME_" + (Math.abs(new Random().nextInt()) % 99999 + 1)

	//CHANGE NAME TILE
	sendEvent(name: "displayName", value: DeviceIP, unit: "")

	//RETURN BUTTONS TO CORRECT STATE
	log.debug 'whichTile: ' + whichTile
    switch (whichTile) {
        case 'refresh':
			sendEvent(name: "refreshswitch", value: "default", isStateChange: true)
			def result = createEvent(name: "refreshswitch", value: "default", isStateChange: true)
			//log.debug "refreshswitch returned ${result?.descriptionText}"
			return result
        case 'RebootNow':
			sendEvent(name: "rebootnow", value: "default", isStateChange: true)
			def result = createEvent(name: "rebootnow", value: "default", isStateChange: true)
			return result
        default:
			sendEvent(name: "refreshswitch", value: "default", isStateChange: true)
			def result = createEvent(name: "refreshswitch", value: "default", isStateChange: true)
			//log.debug "refreshswitch returned ${result?.descriptionText}"
			return result
    }
}

def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
	def nameAndValue = param.split(":")
	map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}
private String convertIPtoHex(ipAddress) { 
	String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
	//log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
	return hex
}
private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
	//log.debug hexport
	return hexport
}
private Integer convertHexToInt(hex) {
	Integer.parseInt(hex,16)
}
private String convertHexToIP(hex) {
	//log.debug("Convert hex to ip: $hex") 
	[convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}
private getHostAddress() {
	def parts = device.deviceNetworkId.split(":")
	//log.debug device.deviceNetworkId
	def ip = convertHexToIP(parts[0])
	def port = convertHexToInt(parts[1])
	return ip + ":" + port
}
