import { Component } from '@angular/core';
import {AppTitleService} from "../shared/services/app-title.service";
import {Project} from "../shared/model/project";

@Component({
  selector: 'view-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
    pageTitle = "";
    currentProject: Project;
    showToolbar = true;

    constructor(private appTitleService: AppTitleService) {
    }

    ngOnInit() {
        // Clear loading mask
        document.getElementById("loadingDiv").style.display = "none";

        this.appTitleService.getTitleObserver().subscribe(title => {
                this.pageTitle = title;
                document.title = title;
                if (title === "Scoreboard") {
                    this.showToolbar = false;
                } else {
                    this.showToolbar = true;
                }
            },
            e => console.log('on error %s', e),
            () => console.log('on complete'));

        this.appTitleService.getCurrentProjectObserver().subscribe(project => this.currentProject = project);
    }
}
