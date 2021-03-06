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
package com.zerocracy.stk.pm

import com.jcabi.xml.XML
import com.zerocracy.Farm
import com.zerocracy.Par
import com.zerocracy.Project
import com.zerocracy.farm.Assume
import com.zerocracy.pm.ClaimIn
import com.zerocracy.pm.ClaimOut
import com.zerocracy.pmo.Catalog
import com.zerocracy.pmo.Pmo

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Publish the project')
  ClaimIn claim = new ClaimIn(xml)
  String mode = claim.param('mode')
  Farm farm = binding.variables.farm
  Catalog catalog = new Catalog(farm)
  if ('on' == mode) {
    catalog.publish(project.pid(), true)
    claim.reply(
      new Par(
        'The project is visible now at the [board](/board), according to §26'
      ).say()
    ).postTo(project)
    new ClaimOut().type('Notify user').token('user;yegor256').param(
      'message', new Par(
        'The project %s was published by @%s'
      ).say(project.pid(), claim.author())
    ).param('cause', claim.cid()).postTo(project)
    new ClaimOut()
      .type('Project was published')
      .param('cause', claim.cid())
      .param('author', claim.author())
      .param('pid', project.pid())
      .postTo(new Pmo(farm))
    new ClaimOut().type('Notify all').param(
      'message',
      new Par('The project %s was published by @%s')
        .say(project.pid(), claim.author())
    ).postTo(project)
  } else if ('off' == mode) {
    catalog.publish(project.pid(), false)
    claim.reply(
      new Par(
        'The project is not visible anymore at the [board](/board), as in §26'
      ).say()
    ).postTo(project)
    new ClaimOut().type('Notify user').token('user;yegor256').param(
      'message', new Par(
        'The project %s was unpublished by @%s'
      ).say(project.pid(), claim.author())
    ).param('cause', claim.cid()).postTo(project)
  } else {
    claim.reply(
      new Par(
        "Incorrect mode, possible values are 'on' or 'off', see §26"
      ).say()
    ).postTo(project)
  }
}
