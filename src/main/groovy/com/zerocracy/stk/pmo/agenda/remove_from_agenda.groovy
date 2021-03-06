/*
 * Copyright (c) 2016-2019 Zerocracy
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
package com.zerocracy.stk.pmo.agenda

import com.jcabi.xml.XML
import com.zerocracy.Farm
import com.zerocracy.Project
import com.zerocracy.claims.ClaimIn
import com.zerocracy.entry.ClaimsOf
import com.zerocracy.farm.Assume
import com.zerocracy.pmo.Agenda

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo().type('Order was finished', 'Order was canceled')
  ClaimIn claim = new ClaimIn(xml)
  if (Boolean.parseBoolean(claim.param('review', Boolean.FALSE.toString()))) {
    // Don't remove jobs from agenda until QA review completed,
    // see `finish_order` and `complete_qa_review`.
    return
  }
  String job = claim.param('job')
  String login = claim.param('login')
  Farm farm = binding.variables.farm
  Agenda agenda = new Agenda(farm, login).bootstrap()
  if (agenda.exists(job) && !agenda.hasInspector(job)) {
    agenda.remove(job)
  }
  claim.copy()
    .type('Agenda was updated')
    .param('login', login)
    .postTo(new ClaimsOf(farm, project))
}
