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
import com.zerocracy.Farm
import com.zerocracy.Project
import com.zerocracy.pm.ClaimIn
import com.zerocracy.pm.ClaimOut

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Order was given')
  ClaimIn claim = new ClaimIn(xml)
  Farm farm = binding.variables.farm
  new ClaimOut()
    .type('Notify user')
    .param('cause', claim.cid())
    .token("user;${claim.param('login')}")
    .param(
      'message',
      new Par(
        farm,
        'The job %s was assigned to you in %s as %s a minute ago,',
        'here is [why](/footprint/%s/%s)'
      ).say(claim.param('job'), project.pid(), claim.param('role'), project.pid(), claim.param('reason'))
    )
    .postTo(project)
}
