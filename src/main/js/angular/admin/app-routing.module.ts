import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AdminHomeComponent} from "./admin-home/admin-home.component";
import {EditProjectComponent} from "./edit-project/edit-project.component";
import {ViewAuditComponent} from "./view-audit/view-audit.component";
import {CreateProjectComponent} from "./create-project/create-project.component";
import {ViewProjectComponent} from "./view-project/view-project.component";
import {EditTeamComponent} from "./edit-team/edit-team.component";
import {ViewMembersComponent} from "./view-members/view-members.component";
import {ViewStudentsComponent} from "./view-students/view-students.component";
import {EditStudentComponent} from "./edit-add-student/edit-student.component";
import {ImportStudentsComponent} from "./import-students/import-students.component";
import {UserPermissionsComponent} from "./user-permissions/user-permissions.component";
import {CreateTeamComponent} from "./create-team/create-team.component";
import {InviteMemberComponent} from "./invite-member/invite-member.component";
import {DocumentsComponent} from "./documents/documents.component";
import {EditAlbumsComponent} from "./edit-albums/edit-albums.component";

const routes: Routes = [
    { path: '', redirectTo: '/admin', pathMatch: 'full' },
    { path: 'admin', component: AdminHomeComponent },
    { path: 'admin/:id', component: AdminHomeComponent },
    { path: 'audit', component: ViewAuditComponent },
    { path: 'audit/:id', component: ViewAuditComponent },
    { path: 'viewproject/:id', component: ViewProjectComponent },
    { path: 'viewteam/:projectId/team/:teamId', component: EditTeamComponent },
    { path: 'newproject', component: CreateProjectComponent },
    { path: 'editproject/:id', component: EditProjectComponent },
    { path: 'viewmembers/:id', component: ViewMembersComponent },
    { path: 'viewstudents/:id', component: ViewStudentsComponent },
    { path: 'editstudent/:projectId', component: EditStudentComponent },
    { path: 'editstudent/:projectId/student/:studentId', component: EditStudentComponent },
    { path: 'importstudents/:id', component: ImportStudentsComponent },
    { path: 'userpermissions/:projectId/user/:userId', component: UserPermissionsComponent },
    { path: 'createteam/:projectId', component: CreateTeamComponent },
    { path: 'invitemember/:projectId/:inviteType', component: InviteMemberComponent },
    { path: 'documents/:id', component: DocumentsComponent },
    { path: 'editalbums/:id', component: EditAlbumsComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
