import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {EditProfileComponent} from "./edit-profile/edit-profile.component";
import {AdminHomeComponent} from "./admin-home/admin-home.component";
import {EditProjectComponent} from "./edit-project/edit-project.component";
import {ViewAuditComponent} from "./view-audit/view-audit.component";
import {CreateProjectComponent} from "./create-project/create-project.component";

const routes: Routes = [
  { path: '', redirectTo: '/admin', pathMatch: 'full' },
  { path: 'admin', component: AdminHomeComponent },
  { path: 'profile', component: EditProfileComponent },
  { path: 'audit', component: ViewAuditComponent },
  { path: 'newproject', component: CreateProjectComponent },
  { path: 'editproject/:key', component: EditProjectComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
