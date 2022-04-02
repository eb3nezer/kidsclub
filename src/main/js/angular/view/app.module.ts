import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {HomeComponent} from "./home/home.component";
import {MatButtonModule} from "@angular/material/button";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatInputModule} from "@angular/material/input";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatListModule} from "@angular/material/list";
import {MatGridListModule} from "@angular/material/grid-list";
import {MatTableModule} from "@angular/material/table";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatSelectModule} from "@angular/material/select";
import {MatDialogModule} from "@angular/material/dialog";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {MatCardModule} from "@angular/material/card";
import {MatButtonToggleModule} from "@angular/material/button-toggle";
import {RouterModule} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientModule} from "@angular/common/http";
import {AppRoutingModule} from "./app-routing.module";
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
import {ConfirmDialogComponent} from "../shared/confirm-dialog/confirm-dialog.component";
import {AttendanceComponent} from "./attendance/attendance.component";

@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
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
        ProjectWallboardComponent,
        AttendanceComponent
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
        MatCardModule,
        MatButtonToggleModule
    ],
    providers: [],
    bootstrap: [AppComponent],
    entryComponents: [ConfirmDialogComponent]
})
export class AppModule { }
