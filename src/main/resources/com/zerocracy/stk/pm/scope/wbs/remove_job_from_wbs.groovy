/**
 * Copyright (c) 2016-2018 Zerocracy
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
package com.zerocracy.stk.pm.scope.wbs

import com.jcabi.xml.XML
import com.zerocracy.Par
import com.zerocracy.farm.Assume
import com.zerocracy.Project
import com.zerocracy.pm.ClaimIn
import com.zerocracy.pm.ClaimOut
import com.zerocracy.pm.in.Orders
import com.zerocracy.pm.scope.Wbs

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Remove job from WBS')
  new Assume(project, xml).roles('ARC', 'PO')
  ClaimIn claim = new ClaimIn(xml)
  String job = claim.param('job')
  Wbs wbs = new Wbs(project).bootstrap()
  Orders orders = new Orders(project).bootstrap()
  if (orders.assigned(job)) {
    String performer = orders.performer(job)
    orders.resign(job)
    claim.reply(
      new Par(
        '@%s resigned from %s, since the job is not in scope anymore'
      ).say(performer, job)
    ).postTo(project)
    new ClaimOut()
      .type('Order was canceled')
      .param('cause', claim.cid())
      .param('job', job)
      .param('voluntarily', false)
      .param('login', performer)
      .postTo(project)
  }
  wbs.remove(job)
  if (claim.hasToken()) {
    claim.reply(
      new Par('The job %s is now out of scope').say(job)
    ).postTo(project)
  }
  new ClaimOut()
    .type('Job removed from WBS')
    .param('cause', claim.cid())
    .param('job', job)
    .postTo(project)
}
