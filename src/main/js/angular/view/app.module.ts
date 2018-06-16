import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {HomeComponent} from "./home/home.component";
import {
    MatAutocompleteModule,
    MatButtonModule, MatCardModule, MatCheckboxModule, MatDialogModule, MatFormFieldModule, MatGridListModule,
    MatIconModule,
    MatInputModule, MatListModule,
    MatMenuModule, MatSelectModule, MatSnackBarModule, MatTableModule,
    MatToolbarModule
} from "@angular/material";
import {RouterModule} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientModule} from "@angular/common/http";
import {AppRoutingModule} from "./app-routing.module";
import {LeaderComponent} from "./leader/leader.component";
import {LeaderTeamComponent} from "./leader/leader-team.component";
import {ProjectComponent} from "./project/project.component";
import {MembersComponent} from "./members/members.component";
import {StudentsComponent} from "./students/students.component";
import {StudentComponent} from "./student/student.component";
import {TeamComponent} from "./team/team.component";
import {DocumentsComponent} from "./documents/documents.component";
import {ConfirmDialogComponent} from "./confirm-dialog/confirm-dialog.component";
import {AlbumsComponent} from "./albums/albums.component";
import {AlbumComponent} from "./album/album.component";
import {EditProfileForViewComponent} from "./edit-profile/edit-profile.component";
import {ProjectWallboardComponent} from "./project/project-wallboard.component";

@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
        LeaderComponent,
        LeaderTeamComponent,
        ProjectComponent,
        MembersComponent,
        StudentsComponent,
        StudentComponent,
        TeamComponent,
        DocumentsComponent,
        ConfirmDialogComponent,
        AlbumsComponent,
        AlbumComponent,
        EditProfileForViewComponent,
        ProjectWallboardComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        FormsModule,
        ReactiveFormsModule,
        MatButtonModule,
        MatCheckboxModule,
        MatMenuModule,
        MatIconModule,
        MatToolbarModule,
        MatInputModule,
        MatFormFieldModule,
        MatListModule,
        MatGridListModule,
        HttpClientModule,
        MatTableModule,
        MatAutocompleteModule,
        MatSelectModule,
        MatDialogModule,
        MatSnackBarModule,
        RouterModule,
        AppRoutingModule,
        MatCardModule
    ],
    providers: [],
    bootstrap: [AppComponent],
    entryComponents: [ConfirmDialogComponent]
})
export class AppModule { }
