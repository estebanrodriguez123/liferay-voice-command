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
import java.util.HashMap;
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
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortletKeys;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.rivetlogic.speech.bean.CommandBean;
import com.rivetlogic.speech.bean.impl.CommandBeanImpl;

public class SpeechPortlet extends MVCPortlet {

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws IOException, PortletException {
        String key;
        List<CommandBean> commandBeans = new ArrayList<CommandBean>();

        PortletPreferences preference = request.getPreferences();

        Map<String, String[]> preferenceMap = preference.getMap();
        for (Map.Entry<String, String[]> entry : preferenceMap.entrySet()) {
            key = entry.getKey().trim();
            if (!key.isEmpty() && !key.equalsIgnoreCase(KEY_WORD)) {
                commandBeans.add(new CommandBeanImpl(entry.getKey(), entry.getValue()[0]));
            }
        }
        request.setAttribute(COMMAND_COUNT, commandBeans.size());
        request.setAttribute(COMMAND_LIST, commandBeans);
        super.doView(request, response);
    }

    /**
     * Action to add new speech command and its value.
     *
     * @param request
     *            ActionRequest
     * @param response
     *            ActionResponse
     * @throws PortalException
     * @throws SystemException
     */
    public void addCommandAction(ActionRequest request, ActionResponse response) throws PortalException,
            SystemException {
        PortletPreferences preference = request.getPreferences();
        try {
            preference.setValue(KEY_WORD, ParamUtil.getString(request, KEY_WORD));
            preference.setValue(ParamUtil.getString(request, COMMAND_KEY), ParamUtil.getString(request, COMMAND_VALUE));
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
     * Return view page
     *
     * @param request
     * @param response
     * @throws PortalException
     * @throws SystemException
     * @throws PortletModeException
     */
    public void backAction(ActionRequest request, ActionResponse response) throws PortalException, SystemException,
            PortletModeException {
        response.setPortletMode(PortletMode.VIEW);
    }

    public void deleteAction(ActionRequest request, ActionResponse response) throws PortalException, SystemException,
            PortletModeException {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        long plid = themeDisplay.getLayout().getPlid();
        long ownerId = PortletKeys.PREFS_OWNER_ID_DEFAULT;
        int ownerType = PortletKeys.PREFS_OWNER_TYPE_LAYOUT;
        long companyId = themeDisplay.getCompanyId();

        String commandKey = ParamUtil.get(request, COMMAND_KEY, StringPool.BLANK);

        PortletPreferences preference = request.getPreferences();
        Map<String, String[]> preferencesMap = new HashMap<String, String[]>(preference.getMap());
        preferencesMap.remove(commandKey);

        PortletPreferencesLocalServiceUtil.deletePortletPreferences(ownerId, ownerType, plid, PORTLET_NAMESPACE);

        preference = PortletPreferencesLocalServiceUtil.getPreferences(companyId, ownerId, ownerType, plid,
                PORTLET_NAMESPACE);
        try {
            for (Map.Entry<String, String[]> entry : preferencesMap.entrySet()) {
                preference.setValue(entry.getKey(), entry.getValue()[0]);
            }
            preference.store();
        } catch (ReadOnlyException e) {
            _log.error(e);
        } catch (ValidatorException e) {
            _log.error(e);
        } catch (IOException e) {
            _log.error(e);
        }
    }

    private static final Log _log = LogFactoryUtil.getLog(SpeechPortlet.class);
    private static final String PORTLET_NAMESPACE = "voicecommands_WAR_voicecommandsportlet";
    private static final String KEY_WORD = "key_word";
    private static final String COMMAND_COUNT = "command_count";
    private static final String COMMAND_LIST = "command_list";
    private static final String COMMAND_KEY = "command_key";
    private static final String COMMAND_VALUE = "command_value";

    private static final String SESSION_MESSAGE_SUCCESS = "rivet_speech_success_msg";
}
