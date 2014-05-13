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

package com.rivetlogic.speech.util;

import java.util.ArrayList;
import java.util.List;

import com.liferay.portal.kernel.util.ListUtil;
import com.rivetlogic.speech.bean.CommandBean;

public class SpeechUtil {
    public static List<CommandBean> subList(List<CommandBean> list, Integer start, Integer end) {
        if (list != null) {
            return ListUtil.subList(list, start, end);
        } else {
            return new ArrayList<CommandBean>();
        }
    }
    
    public static final int NUMBER_OF_ROWS = 10;
}
