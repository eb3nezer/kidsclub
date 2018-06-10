import { Component, OnInit } from '@angular/core';
import { AppTitleService } from "../services/app-title.service";
import { User } from "../model/user";
import { UserProfileService } from "../services/user-profile.service";

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {
  currentUser: User;
  fileToUpload: File;
  newFilename = "";

  constructor(
    private apptitleService: AppTitleService,
    private userProfileService: UserProfileService) {
    this.currentUser = new User();
  }

  onSubmit() {
      console.log("Submitted");
      this.userProfileService.updateCurrentUserObservable(this.currentUser, this.fileToUpload).subscribe(next => {
          this.currentUser = next;
          this.newFilename = "";
          this.apptitleService.setMessages("Your profile was updated successfully.")
      });
  }

    private setFile(event) {
        if (event.srcElement.files && event.srcElement.files.length >= 1) {
            this.fileToUpload = event.srcElement.files[0];
            this.newFilename = this.fileToUpload.name;
        }
    }

  ngOnInit() {
  }

  ngAfterViewInit() {
    Promise.resolve(null).then(() => this.apptitleService.setTitle("Edit your profile"));
    this.userProfileService.getCurrentUserObservable().subscribe(next => this.currentUser = next);
  }
}
