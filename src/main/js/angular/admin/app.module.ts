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
    MatGridListModule,
    MatTableModule,
    MatAutocompleteModule, MatSelectModule, MatDialogModule, MatSnackBarModule
} from "@angular/material";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { AppRoutingModule } from './app-routing.module';
import { AdminHomeComponent } from './admin-home/admin-home.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { EditProjectComponent } from './edit-project/edit-project.component';
import { ViewAuditComponent } from './view-audit/view-audit.component';
import { CreateProjectComponent } from './create-project/create-project.component';
import { HttpClientModule } from "@angular/common/http";
import { ViewProjectComponent } from './view-project/view-project.component';
import { EditTeamComponent } from './edit-team/edit-team.component';
import { ViewMembersComponent } from './view-members/view-members.component';
import { ViewStudentsComponent } from './view-students/view-students.component';
import { EditStudentComponent } from './edit-add-student/edit-student.component';
import { ConfirmDialogComponent} from "../shared/confirm-dialog/confirm-dialog.component";
import { ImportStudentsComponent } from './import-students/import-students.component';
import { UserPermissionsComponent } from './user-permissions/user-permissions.component';
import { CreateTeamComponent } from './create-team/create-team.component';
import { InviteMemberComponent } from './invite-member/invite-member.component';
import { AttendanceComponent } from '../view/attendance/attendance.component';
import { EditAlbumsComponent } from './edit-albums/edit-albums.component';
import { EditDocumentsComponent } from './edit-documents/edit-documents.component';
import { DocumentsComponent } from './documents/documents.component';

@NgModule({
    declarations: [
        AppComponent,
        AdminHomeComponent,
        EditProfileComponent,
        EditProjectComponent,
        ViewAuditComponent,
        CreateProjectComponent,
        ViewProjectComponent,
        EditTeamComponent,
        ViewMembersComponent,
        ViewStudentsComponent,
        EditStudentComponent,
        ConfirmDialogComponent,
        ImportStudentsComponent,
        UserPermissionsComponent,
        CreateTeamComponent,
        InviteMemberComponent,
        AttendanceComponent,
        EditAlbumsComponent,
        EditDocumentsComponent,
        DocumentsComponent
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
        AppRoutingModule,
        MatInputModule,
        MatFormFieldModule,
        MatListModule,
        MatGridListModule,
        HttpClientModule,
        MatTableModule,
        MatAutocompleteModule,
        MatSelectModule,
        MatDialogModule,
        MatSnackBarModule
  ],
    providers: [],
    bootstrap: [AppComponent],
    entryComponents: [ConfirmDialogComponent]
})
export class AppModule { }
