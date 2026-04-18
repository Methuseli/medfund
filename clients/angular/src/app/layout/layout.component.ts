import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { SidebarComponent } from './sidebar/sidebar.component';
import { HeaderComponent } from './header/header.component';
import { NavigationService } from '../core/services/navigation.service';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, SidebarComponent, HeaderComponent],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
})
export class LayoutComponent implements OnInit, OnDestroy {
  collapsed = false;
  private sub?: Subscription;

  constructor(private navService: NavigationService) {}

  ngOnInit(): void {
    this.sub = this.navService.collapsed$.subscribe(
      (c) => (this.collapsed = c)
    );
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
