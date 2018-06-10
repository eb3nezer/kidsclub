import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {EditProfileComponent} from "./edit-profile/edit-profile.component";
import {AdminHomeComponent} from "./admin-home/admin-home.component";
import {EditProjectComponent} from "./edit-project/edit-project.component";
import {ViewAuditComponent} from "./view-audit/view-audit.component";
import {CreateProjectComponent} from "./create-project/create-project.component";
import {ViewProjectComponent} from "./view-project/view-project.component";
import {EditTeamComponent} from "./edit-team/edit-team.component";

const routes: Routes = [
    { path: '', redirectTo: '/admin', pathMatch: 'full' },
    { path: 'admin', component: AdminHomeComponent },
    { path: 'admin/:id', component: AdminHomeComponent },
    { path: 'profile', component: EditProfileComponent },
    { path: 'audit', component: ViewAuditComponent },
    { path: 'audit/:id', component: ViewAuditComponent },
    { path: 'viewproject/:id', component: ViewProjectComponent },
    { path: 'viewteam/:projectId/team/:teamId', component: EditTeamComponent },
    { path: 'newproject', component: CreateProjectComponent },
    { path: 'editproject/:id', component: EditProjectComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
