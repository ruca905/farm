/**
 * Copyright (c) 2016-2017 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.radars.github;

import javax.json.JsonObject;

/**
 * Github issue event.
 * See <a href="https://developer.github.com/v3/activity/events/types/#issuesevent">Github docs</a>.
 * @author Kirill (g4s8.public@gmail.com)
 * @version $Id$
 * @since 0.17
 */
public final class GhIssueEvent {

    /**
     * Event json.
     */
    private final JsonObject evt;

    /**
     * Ctor.
     * @param event Event json
     */
    public GhIssueEvent(final JsonObject event) {
        this.evt = event;
    }

    /**
     * The optional user who was assigned or unassigned from the issue.
     * @return Assignee's login
     */
    public String assignee() {
        return this.evt.getJsonObject("assignee").getString("login");
    }
}
