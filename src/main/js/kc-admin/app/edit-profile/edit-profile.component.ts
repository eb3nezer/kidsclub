import { Component, OnInit } from '@angular/core';
import { ApptitleService } from "../apptitle.service";
import { User } from "../model/user";
import { UserProfileService } from "../user-profile.service";

@Component({
  selector: 'app-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css']
})
export class EditProfileComponent implements OnInit {
  currentUser: User;

  constructor(
    private apptitleService: ApptitleService,
    private userProfileService: UserProfileService) {
    this.currentUser = new User();
  }

  ngOnInit() {
  }

  ngAfterViewInit() {
    Promise.resolve(null).then(() => this.apptitleService.setTitle("Edit your profile"));
    this.userProfileService.getUserObservable().subscribe(next => this.currentUser = next);
  }
}
