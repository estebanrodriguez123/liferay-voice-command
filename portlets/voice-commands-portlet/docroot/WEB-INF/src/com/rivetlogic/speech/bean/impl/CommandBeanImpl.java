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

package com.rivetlogic.speech.bean.impl;

import com.rivetlogic.speech.bean.CommandBean;

public class CommandBeanImpl implements CommandBean {
    protected String commandKey;
    protected String commandValue;

    public CommandBeanImpl() {

    }

    public CommandBeanImpl(String commandKey, String commandValue) {
        super();
        this.commandKey = commandKey;
        this.commandValue = commandValue;
    }

    /**
     * @return the commandKey
     */
    public String getCommandKey() {
        return commandKey;
    }

    /**
     * @param commandKey
     *            the commandKey to set
     */
    public void setCommandKey(String commandKey) {
        this.commandKey = commandKey;
    }

    /**
     * @return the commandValue
     */
    public String getCommandValue() {
        return commandValue;
    }

    /**
     * @param commandValue
     *            the commandValue to set
     */
    public void setCommandValue(String commandValue) {
        this.commandValue = commandValue;
    }
}
