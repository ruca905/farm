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
package com.zerocracy.bundles.dont_kickout_graduates_from_sandbox

import com.jcabi.xml.XML
import com.mongodb.client.model.Filters
import com.zerocracy.Farm
import com.zerocracy.Project
import com.zerocracy.claims.Footprint
import com.zerocracy.pm.staff.Roles
import com.zerocracy.pmo.People
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.collection.IsEmptyIterable
import org.hamcrest.core.IsCollectionContaining
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot

def exec(Project project, XML xml) {
  Farm farm = binding.variables.farm
  Roles roles = new Roles(project).bootstrap()
  MatcherAssert.assertThat(
    'Must not remove DEV role from paulodamaso',
    roles.allRoles('paulodamaso'),
    new IsCollectionContaining<>(
      new IsEqual('DEV')
    )
  )
  MatcherAssert.assertThat(
    'Must not remove REV role from paulodamaso',
    roles.allRoles('paulodamaso'),
    new IsCollectionContaining<>(
      new IsEqual('REV')
    )
  )
}
