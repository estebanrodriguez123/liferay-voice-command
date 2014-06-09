<%--
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
 */
--%>

<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.portlet.LiferayPortletMode"%>
<%@page import="com.rivetlogic.speech.util.SpeechConstants"%>
<%@include file="init.jsp" %>
<%
boolean edit = false;
PortletPreferences preference = renderRequest.getPreferences();
String keyWord = preference.getValue(SpeechConstants.KEY_PHRASE, StringPool.BLANK);

String commandKey = ParamUtil.getString(request, SpeechConstants.VOICE_COMMAND, StringPool.BLANK);
String commandValue = ParamUtil.getString(request, SpeechConstants.VOICE_COMMAND_VALUE, StringPool.BLANK);

if(commandKey != null && !commandKey.trim().isEmpty()) {
    edit = true;
}
%>
<!-- MESSAGES -->
<liferay-ui:success key="rivet_speech_success_msg" message="add-command-success-msg"/>

<!-- ACTION URL -->
<portlet:actionURL name="addCommandAction" var="addCommandURL">
	<portlet:param name="delete_voice_command" value="<%= commandKey %>"/>
</portlet:actionURL>
<portlet:actionURL var="backURL" portletMode="<%= LiferayPortletMode.VIEW.toString() %>" windowState="<%= LiferayWindowState.NORMAL.toString() %>">
	<portlet:param name="jspPage" value="/html/voice-commands/view.jsp" />
</portlet:actionURL>
<!-- FORM -->
<aui:form action="${addCommandURL}">
	<aui:fieldset column="true" label='<%= edit ? "edit-command" : "add-command" %>'>
		<aui:input name="key_phrase" title="key_word_help_msg" value="<%= keyWord %>">
			<aui:validator name="maxLength">
                	'15'
            </aui:validator>
		</aui:input>
		<aui:fieldset column="false">
			<aui:input name="voice_command" required="true" title="command_key_help_msg" value="<%= commandKey %>">
				<aui:validator name="maxLength">
                	'35'
            	</aui:validator>
			</aui:input>
			<aui:input name="voice_command_value" required="true" title="command_value_help_msg" value="<%= commandValue %>"/>
		</aui:fieldset>
	</aui:fieldset>
	<aui:button-row>
		<aui:button type="cancel" value="Back" onClick="${backURL}"/>
		<aui:button type="submit" value='<%= edit ? "update-button" : "add-button" %>'/>
	</aui:button-row>
</aui:form>
<aui:script use="aui-tooltip">
    new A.TooltipDelegate(
      {
        trigger: '#<portlet:namespace/>fm input[type=text]',
        position: 'right'
      }
    );
</aui:script>
