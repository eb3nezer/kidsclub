import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {
  MatButtonModule,
  MatCheckboxModule,
  MatIconModule,
  MatMenuModule,
  MatToolbarModule,
  MatInputModule,
  MatFormFieldModule,
  MatListModule,
  MatGridListModule
} from "@angular/material";
import {FormsModule} from "@angular/forms";
import { AppRoutingModule } from './/app-routing.module';
import { AdminHomeComponent } from './admin-home/admin-home.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { EditProjectComponent } from './edit-project/edit-project.component';
import { ViewAuditComponent } from './view-audit/view-audit.component';
import { CreateProjectComponent } from './create-project/create-project.component';
import {HttpClientModule} from "@angular/common/http";

@NgModule({
  declarations: [
    AppComponent,
    AdminHomeComponent,
    EditProfileComponent,
    EditProjectComponent,
    ViewAuditComponent,
    CreateProjectComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatMenuModule,
    MatIconModule,
    MatToolbarModule,
    AppRoutingModule,
    MatInputModule,
    MatFormFieldModule,
    MatListModule,
    MatGridListModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
