import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {HomeComponent} from "./home/home.component";
import {
    MatAutocompleteModule,
    MatButtonModule, MatCheckboxModule, MatDialogModule, MatFormFieldModule, MatGridListModule,
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

@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
        LeaderComponent,
        LeaderTeamComponent,
        ProjectComponent
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
        AppRoutingModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule { }
