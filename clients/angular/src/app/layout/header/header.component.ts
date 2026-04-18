import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { Subscription, filter, map } from 'rxjs';
import { NavigationService } from '../../core/services/navigation.service';
import { UserInfo } from '../../core/models/navigation.model';
import { IconComponent } from '../../shared/components/icon/icon.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, IconComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent implements OnInit, OnDestroy {
  pageTitle = 'Dashboard';
  userInfo: UserInfo = { fullName: 'User', initials: 'U', email: '', roleLabel: 'User' };
  userMenuOpen = false;

  private sub?: Subscription;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private navService: NavigationService,
  ) {}

  ngOnInit(): void {
    this.userInfo = this.navService.getUserInfo();

    this.sub = this.router.events
      .pipe(
        filter((e) => e instanceof NavigationEnd),
        map(() => {
          let r = this.route;
          while (r.firstChild) r = r.firstChild;
          return r.snapshot.data['title'] || this.deriveTitle(this.router.url);
        })
      )
      .subscribe((title) => (this.pageTitle = title));

    // Set initial title
    let r = this.route;
    while (r.firstChild) r = r.firstChild;
    this.pageTitle = r.snapshot.data['title'] || this.deriveTitle(this.router.url);
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  toggleSidebar(): void {
    this.navService.toggleSidebar();
  }

  private deriveTitle(url: string): string {
    const segment = url.split('/').filter(Boolean).pop() || 'dashboard';
    return segment.charAt(0).toUpperCase() + segment.slice(1).replace(/-/g, ' ');
  }
}
