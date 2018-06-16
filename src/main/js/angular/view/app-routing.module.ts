import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {LeaderComponent} from "./leader/leader.component";
import {ProjectComponent} from "./project/project.component";
import {MembersComponent} from "./members/members.component";
import {StudentsComponent} from "./students/students.component";
import {StudentComponent} from "./student/student.component";
import {TeamComponent} from "./team/team.component";
import {DocumentsComponent} from "./documents/documents.component";
import {AlbumsComponent} from "./albums/albums.component";
import {AlbumComponent} from "./album/album.component";
import {EditProfileForViewComponent} from "./edit-profile/edit-profile.component";
import {ProjectWallboardComponent} from "./project/project-wallboard.component";

const routes: Routes = [
    { path: '', redirectTo: '/home', pathMatch: 'full' },
    { path: 'home', component: HomeComponent },
    { path: 'home/:id', component: HomeComponent },
    { path: 'leader/:id', component: LeaderComponent },
    { path: 'project/:id', component: ProjectComponent },
    { path: 'wallboard/:id', component: ProjectWallboardComponent },
    { path: 'members/:id', component: MembersComponent },
    { path: 'students/:id', component: StudentsComponent },
    { path: 'student/:projectId/:studentId', component: StudentComponent },
    { path: 'team/:projectId/:teamId', component: TeamComponent },
    { path: 'documents/:projectId', component: DocumentsComponent },
    { path: 'photos/:projectId', component: AlbumsComponent },
    { path: 'album/:projectId/:albumId', component: AlbumComponent },
    { path: 'profile', component: EditProfileForViewComponent },
    { path: 'profile/:id', component: EditProfileForViewComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
