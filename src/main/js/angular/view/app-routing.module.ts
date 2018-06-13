import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {LeaderComponent} from "./leader/leader.component";
import {ProjectComponent} from "./project/project.component";

const routes: Routes = [
    { path: '', redirectTo: '/home', pathMatch: 'full' },
    { path: 'home', component: HomeComponent },
    { path: 'home/:id', component: HomeComponent },
    { path: 'leader/:id', component: LeaderComponent },
    { path: 'project/:id', component: ProjectComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
