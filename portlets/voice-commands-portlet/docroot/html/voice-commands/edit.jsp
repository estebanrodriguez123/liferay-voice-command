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

<%@include file="init.jsp" %>
<%
PortletPreferences preference = renderRequest.getPreferences();
String keyWord = preference.getValue("key_word", StringPool.BLANK);

String commandKey = ParamUtil.get(request, "command_key", StringPool.BLANK);
String commandValue = ParamUtil.get(request, "command_value", StringPool.BLANK);
%>
<!-- MESSAGES -->
<liferay-ui:success key="rivet_speech_success_msg" message="add-command-success-msg"/>

<!-- ACTION URL -->
<portlet:actionURL name="addCommandAction" var="addCommandURL"/>
<portlet:actionURL name="backAction" var="backURL"/>

<!-- FORM -->
<aui:form action="${addCommandURL}">
	<aui:fieldset column="true" label="add-command">
		<aui:input name="key_word" helpMessage="key_word_help_msg" value="<%= keyWord %>">
			<aui:validator name="maxLength">
                	'15'
            </aui:validator>
		</aui:input>
		<aui:fieldset column="false">
			<aui:input name="command_key" required="true" helpMessage="command_key_help_msg" value="<%= commandKey %>">
				<aui:validator name="maxLength">
                	'35'
            	</aui:validator>
			</aui:input>
			<aui:input name="command_value" required="true" helpMessage="command_value_help_msg" value="<%= commandValue %>"/>
		</aui:fieldset>
	</aui:fieldset>
	<aui:button-row>
		<aui:button type="button" value="Back" onClick="${backURL}"/>
		<aui:button type="submit" value="Add"/>
	</aui:button-row>
</aui:form>

