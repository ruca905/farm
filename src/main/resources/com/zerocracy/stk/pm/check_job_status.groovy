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

import com.jcabi.log.Logger
import com.jcabi.xml.XML
import com.zerocracy.Par
import com.zerocracy.Project
import com.zerocracy.farm.Assume
import com.zerocracy.pm.ClaimIn
import com.zerocracy.pm.cost.Boosts
import com.zerocracy.pm.cost.Estimates
import com.zerocracy.pm.cost.Rates
import com.zerocracy.pm.cost.Vesting
import com.zerocracy.pm.in.Impediments
import com.zerocracy.pm.in.Orders
import com.zerocracy.pm.scope.Wbs
import com.zerocracy.pm.staff.Bans
import org.cactoos.list.ListOf

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Check job status')
  ClaimIn claim = new ClaimIn(xml)
  String job = claim.param('job')
  Collection<String> items = []
  Wbs wbs = new Wbs(project).bootstrap()
  if (wbs.exists(job)) {
    items.add(
      new Par(
        'The job %s is [in scope](http://datum.zerocracy.com/pages/policy.html#14) for ' +
        Logger.format(
          '%[ms]s',
          System.currentTimeMillis() - wbs.created(job).time
        )
      ).say(job)
    )
    items.add(new Par('The role is %s').say(wbs.role(job)))
    Orders orders = new Orders(project).bootstrap()
    if (orders.assigned(job)) {
      String performer = orders.performer(job)
      items.add(
        new Par(
          'The job is assigned to @%s for [' +
            Logger.format(
              '%[ms]s',
              System.currentTimeMillis() - orders.startTime(job).time
            ) +
            '](http://datum.zerocracy.com/pages/policy.html#8)'
        ).say(performer)
      )
      Vesting vesting = new Vesting(project).bootstrap()
      Estimates estimates = new Estimates(project).bootstrap()
      if (estimates.exists(job)) {
        items.add(
          new Par(
            'There is a monetary reward attached'
          ).say()
        )
        Rates rates = new Rates(project).bootstrap()
        if (rates.exists(performer)) {
          items.add(
            new Par(
              '@%s will get %.0f%% of their hourly rate for this job'
            ).say(performer, 100.0d * (estimates.get(job) / rates.rate(performer)))
          )
        }
      } else {
        items.add(
          new Par(
            'There is no monetary reward attached, it\'s a [free](http://datum.zerocracy.com/pages/policy.html#2) job'
          ).say()
        )
      }
      if (vesting.exists(orders.performer(job))) {
        items.add(
          new Par(
            'Some equity will be [vested](http://datum.zerocracy.com/pages/policy.html#37) on completion'
          ).say()
        )
      }
      Impediments impediments = new Impediments(project).bootstrap()
      if (new ListOf<>(impediments.jobs()).contains(job)) {
        items.add(
          new Par(
            'The job has an [impediment](http://datum.zerocracy.com/pages/policy.html#9)'
          ).say()
        )
      } else {
        items.add(
          new Par(
            'The job doesn\'t have any [impediments](http://datum.zerocracy.com/pages/policy.html#9)'
          ).say()
        )
      }
    } else {
      items.add(new Par('The job is not assigned to anyone').say())
    }
    Boosts boosts = new Boosts(project).bootstrap()
    items.add(
      new Par(
        'The [budget](http://datum.zerocracy.com/pages/policy.html#4) is ' +
          boosts.factor(job) * 15 + ' minutes/points'
      ).say()
    )
  } else {
    items.add(new Par('The job %s is not in scope').say(job))
  }
  Bans bans = new Bans(project).bootstrap()
  if (!bans.reasons(job).empty) {
    items.add(
      new Par(
        'These users are banned and won\'t be assigned:\n    * ' +
        new Par.ToText(bans.reasons(job).join('\n    * ')).toString()
      ).say()
    )
  }
  claim.reply(
    new Par(
      'This is what I know about this job, as in §32:'
    ).say() + '\n\n  * ' + items.join('\n  * ')
  ).postTo(project)
}
