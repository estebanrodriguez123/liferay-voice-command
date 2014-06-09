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

package com.rivetlogic.speech.portlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.rivetlogic.speech.bean.CommandBean;
import com.rivetlogic.speech.bean.impl.CommandBeanImpl;
import com.rivetlogic.speech.util.SpeechConstants;
import com.rivetlogic.speech.util.SpeechUtil;

public class SpeechPortlet extends MVCPortlet {

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        String key;
        List<CommandBean> commandBeans = new ArrayList<CommandBean>();

        PortletPreferences preference = request.getPreferences();

        Map<String, String[]> preferenceMap = preference.getMap();
        for (Map.Entry<String, String[]> entry : preferenceMap.entrySet()) {
            key = entry.getKey().trim();
            if (!key.isEmpty() && !key.equalsIgnoreCase(SpeechConstants.KEY_PHRASE)) {
                commandBeans.add(new CommandBeanImpl(entry.getKey(), entry.getValue()[0]));
            }
        }
        request.setAttribute(SpeechConstants.VOICE_COMMAND_COUNT, commandBeans.size());
        request.setAttribute(SpeechConstants.VOICE_COMMAND_LIST, commandBeans);
        SessionMessages.clear(request);
        super.doView(request, response);
    }

    /**
     * Action to add new speech command and its value.
     *
     * @param request ActionRequest
     * @param response ActionResponse
     * @throws PortalException
     * @throws SystemException
     */
    public void addCommandAction(ActionRequest request, ActionResponse response) throws PortalException,
            SystemException {
        PortletPreferences preference;
        try {
            String voiceCommand = ParamUtil.getString(request, SpeechConstants.DELETE_VOICE_COMMAND);
            if(voiceCommand != null && !voiceCommand.trim().isEmpty()) {
                preference = SpeechUtil.deleteVoiceCommand(request, voiceCommand);
            } else {
                preference = request.getPreferences();
            }
            preference.setValue(SpeechConstants.KEY_PHRASE, ParamUtil.getString(request, SpeechConstants.KEY_PHRASE));
            preference.setValue(ParamUtil.getString(request, SpeechConstants.VOICE_COMMAND), 
                    ParamUtil.getString(request, SpeechConstants.VOICE_COMMAND_VALUE));
            preference.store();

            SessionMessages.add(request, SESSION_MESSAGE_SUCCESS);
        } catch (ReadOnlyException e) {
            _log.error(e);
        } catch (ValidatorException e) {
            _log.error(e);
        } catch (IOException e) {
            _log.error(e);
        }
    }

    /**
     * Delete a voice command.
     * @param request ActionRequest
     * @param response ActionResponse
     * @throws PortalException
     * @throws SystemException
     * @throws PortletModeException
     */
    public void deleteAction(ActionRequest request, ActionResponse response) 
            throws PortalException, SystemException,
            PortletModeException {
        try {
            SpeechUtil.deleteVoiceCommand(request);
        } catch (ReadOnlyException e) {
            _log.error(e);
        } catch (ValidatorException e) {
            _log.error(e);
        } catch (IOException e) {
            _log.error(e);
        }
    }

    private static final Log _log = LogFactoryUtil.getLog(SpeechPortlet.class);

    private static final String SESSION_MESSAGE_SUCCESS = "rivet_speech_success_msg";
}
