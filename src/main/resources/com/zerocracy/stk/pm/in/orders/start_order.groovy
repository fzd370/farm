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
package com.zerocracy.stk.pm.in.orders

import com.jcabi.xml.XML
import com.zerocracy.Par
import com.zerocracy.farm.Assume
import com.zerocracy.Project
import com.zerocracy.pm.ClaimIn
import com.zerocracy.pm.ClaimOut
import com.zerocracy.pm.in.Orders
import com.zerocracy.pm.scope.Wbs
import com.zerocracy.pm.staff.Roles
import com.zerocracy.pmo.People

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Start order')
  ClaimIn claim = new ClaimIn(xml)
  String job = claim.param('job')
  String login = claim.param('login')
  String reason = claim.param('reason')
  Orders orders = new Orders(project).bootstrap()
  orders.assign(job, login, reason)
  String role = new Wbs(project).bootstrap().role(job)
  String msg
  if (role == 'REV') {
    String arc = new Roles(project).bootstrap().findByRole('ARC')[0]
    msg = new Par(
      'This pull request %s is assigned to @%s, here is',
      '[why](/footprint/%s/%s).',
      'The budget is 15 minutes, see §4.',
      'Please, read §27 and',
      'when you decide to accept the changes,',
      'inform @%s (the architect) right in this ticket.',
      'If you decide that this PR should not be accepted ever,',
      'also inform the architect.'
    ).say(job, login, project.pid(), claim.cid(), arc)
  } else {
    msg = new Par(
      'The job %s assigned to @%s, here is',
      '[why](/footprint/%s/%s).',
      'The budget is 30 minutes, see §4.',
      'Please, read §8 and §9.',
      'If the task is not clear,',
      'read [this](/2015/02/16/it-is-not-a-school.html)',
      'and [this](/2015/01/15/how-to-cut-corners.html).'
    ).say(job, login, project.pid(), claim.cid())
  }
  if (!new Roles(project).bootstrap().hasAnyRole(login)) {
    msg += new Par(
      ' @%s is not a member of this project yet,',
      'but they can request to join.',
      'Check your [profile](/u/%1$s) and follow the instructions.'
    ).say(login)
  }
  if (new People(project).bootstrap().vacation(login)) {
    msg += new Par(
      'We should be aware that %s is on vacation!',
      'This ticket may be delayed.'
    ).say(login)
  }
  claim.reply(msg).postTo(project)
  new ClaimOut()
    .type('Order was given')
    .param('cause', claim.cid())
    .param('job', job)
    .param('role', role)
    .param('login', login)
    .param('reason', claim.cid())
    .postTo(project)
}
